package gitflow.actions;

import com.intellij.openapi.actionSystem.ActionUiKind;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.project.Project;
import git4idea.actions.GitResolveConflictsAction;

/**
 * All actions associated with Gitflow
 *
 * @author Opher Vishnia / opherv.com / opherv@gmail.com
 */
public class GitflowActions {

    public static void runMergeTool(Project project){
        GitResolveConflictsAction action = new GitResolveConflictsAction();
        ActionUtil.performAction(action, AnActionEvent.createEvent(action, SimpleDataContext.getProjectContext(project), null, ActionPlaces.UNKNOWN, ActionUiKind.NONE, null));
    }
}
