/**
 * @author Aswin Rajkumar <aswin.rajkumar@usc.edu>
 */ 
package edu.usc.cssl.nlputils.plugins.svmClassifier.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

public class openSvmHandler {
	@Inject
	private EPartService partService;
	@Execute
	public void execute() {
		System.out.println("Opening SVM");
		MPart part = partService.findPart("edu.usc.cssl.nlputils.plugins.svmClassifier.parts.SvmClassifierSettings");
		part.setVisible(true);
		partService.showPart(part, PartState.VISIBLE);
	}
		
}