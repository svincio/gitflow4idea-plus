package gitflow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class maps repos to their corresponding branch utils
 * Note that the static class is used across projects
 */

public class GitflowBranchUtilManager {
    // accessed from the EDT and from pooled threads (widget refresh)
    private static final Map<String, GitflowBranchUtil> repoBranchUtilMap = new ConcurrentHashMap<>();

    static public GitflowBranchUtil getBranchUtil(GitRepository repo){
        if (repo == null) {
            return null;
        }
        GitflowBranchUtil branchUtil = repoBranchUtilMap.get(repo.getPresentableUrl());
        if (branchUtil == null) {
            // not populated yet by the widget (#42): build it on demand
            setupBranchUtil(repo.getProject(), repo);
            branchUtil = repoBranchUtilMap.get(repo.getPresentableUrl());
        }
        return branchUtil;
    }

    static public void setupBranchUtil(Project project, GitRepository repo){
        GitflowBranchUtil gitflowBranchUtil = new GitflowBranchUtil(project, repo);
        String key = repo.getPresentableUrl();
        // clean up (registered once per repo, not on every refresh)
        if (repoBranchUtilMap.put(key, gitflowBranchUtil) == null) {
            Disposer.register(repo, () -> repoBranchUtilMap.remove(key));
        }
    }

    /**
     * Repopulates the branchUtils for each repo
     * @param proj
     */
    static public void update(Project proj){
        List<GitRepository> gitRepositories = GitUtil.getRepositoryManager(proj).getRepositories();
        for (GitRepository repo : gitRepositories) {
            GitflowBranchUtilManager.setupBranchUtil(proj, repo);
        }
    }
}
