package gitflow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFileManager;
import git4idea.branch.GitBranchUtil;
import git4idea.merge.GitConflictResolver;
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
import java.util.Collections;

public abstract class GitflowAction extends DumbAwareAction {

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

    protected void setup(Project project){
        myProject = project;
        virtualFileMananger = VirtualFileManager.getInstance();
        repos.add(myRepo);

        branchUtil= GitflowBranchUtilManager.getBranchUtil(myRepo);
    }

    public void runAction(final Project project, final String baseBranchName, final String branchName, @Nullable final Runnable callInAwtLater){
        setup(project);
    }

    /**
     * Opens the git4idea merge dialog for the conflicts left by a gitflow finish command. When all conflicts are
     * resolved, the merge commit is created and {@code onMergeCommitted} runs on the EDT (e.g. to resume the
     * finish command). Everything but the dialog runs in background, as git4idea requires since 2026.x.
     */
    public void handleMerge(final Project project, final Runnable onMergeCommitted) {
        new Task.Backgroundable(project, "Resolving merge conflicts", false) {
            private boolean committed;

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitConflictResolver.Params params = new GitConflictResolver.Params(project)
                        .setErrorNotificationTitle("Gitflow: merge conflicts not resolved")
                        .setMergeDescription("Resolve the conflicts to complete the gitflow operation.");
                // merge() shows the dialog and notifies the user itself if conflicts remain unresolved
                if (new GitConflictResolver(project, Collections.singletonList(myRepo.getRoot()), params).merge()) {
                    try {
                        new GitMerger(project).mergeCommit(myRepo.getRoot());
                        committed = true;
                    } catch (VcsException e) {
                        NotifyUtil.notifyError(project, "Error", "Error committing merge result: " + e.getMessage());
                    }
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

    protected void runReadAction(Runnable runnable) {
        Application application = ApplicationManager.getApplication();
        application.runReadAction(runnable);
    }

}
