package edu.usc.cssl.nlputils.classify.svm.services;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import edu.usc.cssl.nlputils.common.ui.views.ConsoleView;

public class CrossValidator {

	public void doCross(SVMClassify svm, String class1Label, File[] class1Files, String class2Label, File[] class2Files, int kValue, boolean doPredictiveWeights, IProgressMonitor monitor) throws IOException{
//		File folder1 = new File(class1Folder);
//		File folder2 = new File(class2Folder);
//		File[] class1Files = folder1.listFiles();
//		File[] class2Files = folder2.listFiles();
		int numFiles1 = class1Files.length;
		int numFiles2 = class2Files.length;
		
		int trains1 = (int)Math.floor(0.90 * numFiles1);
		int trains2 = (int)Math.floor(0.90 * numFiles2);
		
		double[] accuracies = new double[kValue];
		
		int index1 = 0;
		int index2 = 0;
		
		for (int i=1; i<=kValue;i++){
			ConsoleView.printlInConsoleln("--- Fold "+i+" ---");
			File[] trainFiles1 = new File[trains1];
			File[] trainFiles2 = new File[trains2];
			File[] testFiles1 = new File[numFiles1-trains1];
			File[] testFiles2 = new File[numFiles2-trains2];
			
			int currIndex = index1;
			for (int num =0; num<trains1;num++){
				trainFiles1[num]=class1Files[currIndex];
				//ConsoleView.writeInConsole(files1[currIndex]);
				currIndex++;
				if(currIndex >= numFiles1)
					currIndex=0;
			}
			for (int num =0; num < numFiles1-trains1;num++){
				testFiles1[num]=class1Files[currIndex];
				//ConsoleView.writeInConsole(files1[currIndex]);
				currIndex++;
				if(currIndex >= numFiles1)
					currIndex=0;
			}
			
			currIndex = index2;
			for (int num =0; num<trains2;num++){
				trainFiles2[num]=class2Files[currIndex];
				//ConsoleView.writeInConsole(files2[currIndex]);
				currIndex++;
				if(currIndex >= numFiles2)
					currIndex=0;
			}
			for (int num =0; num < numFiles2-trains2;num++){
				testFiles2[num]=class2Files[currIndex];
				//ConsoleView.writeInConsole(files1[currIndex]);
				currIndex++;
				if(currIndex >= numFiles1)
					currIndex=0;
			}
			
			svm.cross_train("k"+i,class1Label, trainFiles1, class2Label, trainFiles2, doPredictiveWeights);
			accuracies[i-1]=svm.cross_predict("k"+i,class1Label, testFiles1, class2Label, testFiles2);
			
			// Clear required globals like dfmap?
			index1 = index1 + numFiles1-trains1;
			if (index1 >= numFiles1){
				index1 = index1 - numFiles1;
			}
			
			index2 = index2 + numFiles2-trains2;
			if (index2 >= numFiles2){
				index2 = index2 - numFiles2;
			}
			monitor.worked(1);
		}
		
		double averageAccuracy = 0;
		
		for (int j = 0; j < accuracies.length; j++) {
			averageAccuracy = averageAccuracy+accuracies[j];
		}
		
		ConsoleView.printlInConsoleln("");
		ConsoleView.printlInConsoleln("Average accuracy over "+kValue+" folds = "+averageAccuracy/accuracies.length+"%");
	}
}