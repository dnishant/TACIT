package edu.usc.cssl.tacit.topicmodel.hlda.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.usc.cssl.tacit.topicmodel.hlda.ui.HeirarchicalLDAView;

public class HeirarchicalLDAViewHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			HandlerUtil.getActiveWorkbenchWindowChecked(event).
			getActivePage().showView(HeirarchicalLDAView.ID);

		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

}
