package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFileManager;
import git4idea.branch.GitBranchUtil;
import git4idea.merge.GitMerger;
import git4idea.repo.GitRepository;
import gitflow.Gitflow;
import gitflow.GitflowBranchUtil;
import gitflow.GitflowBranchUtilManager;
import gitflow.IDEAUtils;
import gitflow.ui.NotifyUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class GitflowAction extends DumbAwareAction {

    private static final long VFM_REFRESH_DELAY = 750L;

    Project myProject;
    Gitflow myGitflow = ApplicationManager.getApplication().getService(Gitflow.class);
    ArrayList<GitRepository> repos = new ArrayList<GitRepository>();
    GitRepository myRepo;
    GitflowBranchUtil branchUtil;

    VirtualFileManager virtualFileMananger;

    GitflowAction( String actionName){ super(actionName); }

    GitflowAction(GitRepository repo, String actionName){
        super(actionName);
        myRepo = repo;

        branchUtil = GitflowBranchUtilManager.getBranchUtil(myRepo);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = IDEAUtils.getActiveProject();

        // if repo isn't set explicitly, such as in the case of starting from keyboard shortcut, infer it
        if (myRepo == null){
            myRepo = GitBranchUtil.guessRepositoryForOperation(project, e.getDataContext());
        }
        setup(project);
    }

    private void setup(Project project){
        myProject = project;
        virtualFileMananger = VirtualFileManager.getInstance();
        repos.add(myRepo);

        branchUtil= GitflowBranchUtilManager.getBranchUtil(myRepo);
    }

    public void runAction(final Project project, final String baseBranchName, final String branchName, @Nullable final Runnable callInAwtLater){
        setup(project);
    }

    /**
     * Runs the merge tool on the EDT, then asks the user whether the merge is complete. If so, the merge commit
     * and the repository refresh run in background (both are forbidden on the EDT since 2026.x) and
     * {@code onMergeCommitted} is invoked on the EDT afterwards, e.g. to resume the gitflow finish command.
     */
    public void handleMerge(final Project project, final Runnable onMergeCommitted) {
        // FIXME As of 201.0 the async version of this method still doesn't make use of the callback, else we'd
        //  simply use a FutureTask (which is a Runnable) and its get() method prior to launching the merge tool.
        virtualFileMananger.syncRefresh();

        try {
            long start, end = System.currentTimeMillis();
            do {
                start = end;
                // Hence this ugly hack, to let the time to intellij to catch up with the external changes made by the
                // CLI before being able to run the merge tool. Else the tool won't have the right state, won't display,
                // and the merge success Y/N dialog will appear directly! Anyway, in v193 500ms was sufficient,
                // but in v201 the right value seems to be in the [700-750]ms range (on the committer's machine).
                Thread.sleep(VFM_REFRESH_DELAY);

                GitflowActions.runMergeTool(project); // The window is modal, so we can measure how long it's opened.
                end = System.currentTimeMillis();
            } while(end - start < 1000L); // Additional hack: a window open <1s obviously didn't open, let's try again.

            // And refreshing again so an onscreen file doesn't show in a conflicted state when the Y/N dialog show up.
            virtualFileMananger.syncRefresh();
            Thread.sleep(VFM_REFRESH_DELAY);
        }
        catch (InterruptedException ignored) {
            return;
        }

        if (!askUserForMergeSuccess(project)) {
            return;
        }

        new Task.Backgroundable(project, "Committing merge result", false) {
            private boolean committed;

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitMerger gitMerger = new GitMerger(project);
                try {
                    gitMerger.mergeCommit(gitMerger.getMergingRoots());
                    committed = true;
                } catch (VcsException e) {
                    NotifyUtil.notifyError(project, "Error", "Error committing merge result: " + e.getMessage());
                }
                myRepo.update();
            }

            @Override
            public void onSuccess() {
                if (committed) {
                    onMergeCommitted.run();
                }
            }
        }.queue();
    }

    private static boolean askUserForMergeSuccess(Project myProject) {
        //if merge was completed successfully, finish the action
        //note that if it wasn't intellij is left in the "merging state", and git4idea provides no UI way to resolve it
        //merging can be done via intellij itself or any other util
        int answer = Messages.showYesNoDialog(myProject, "Was the merge completed succesfully?", "Merge", Messages.getQuestionIcon());
        if (answer==0){
            return true;
        }
        else{

	        NotifyUtil.notifyInfo(myProject,"Merge incomplete","To manually complete the merge choose VCS > Git > Resolve Conflicts.\n" +
			        "Once done, commit the merged files.\n");
            return false;
        }


    }

    protected void runReadAction(Runnable runnable) {
        Application application = ApplicationManager.getApplication();
        application.runReadAction(runnable);
    }

}
