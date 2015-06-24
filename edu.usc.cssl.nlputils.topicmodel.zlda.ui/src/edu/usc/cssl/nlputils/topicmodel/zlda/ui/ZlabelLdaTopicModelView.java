package edu.usc.cssl.nlputils.topicmodel.zlda.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import edu.usc.cssl.nlputils.common.ui.composite.from.NlputilsFormComposite;
import edu.usc.cssl.nlputils.common.ui.outputdata.OutputLayoutData;
import edu.usc.cssl.nlputils.common.ui.validation.OutputPathValidation;
import edu.usc.cssl.nlputils.topicmodel.zlda.services.ZlabelTopicModelAnalysis;
import edu.usc.cssl.nlputils.topicmodel.zlda.ui.internal.IZlabelLdaTopicModelClusterViewConstants;
import edu.usc.cssl.nlputils.topicmodel.zlda.ui.internal.ZlabelLdaTopicModelViewImageRegistry;
import edu.usc.nlputils.common.Preprocess;

public class ZlabelLdaTopicModelView extends ViewPart implements
		IZlabelLdaTopicModelClusterViewConstants {
	public static final String ID = "edu.usc.cssl.nlputils.topicmodel.zlda.ui.view1";
	private ScrolledForm form;
	private FormToolkit toolkit;
	private Button preprocessEnabled;

	private OutputLayoutData layoutData;
	private OutputLayoutData inputLayoutData;
	private Text seedFileText;
	private Text topics;
	private Button fAddFileButton;
	protected Job job;

	@Override
	public void createPartControl(Composite parent) {
		toolkit = createFormBodySection(parent);
		Section section = toolkit.createSection(form.getBody(),
				Section.TITLE_BAR | Section.EXPANDED);

		GridDataFactory.fillDefaults().grab(true, false).span(3, 1)
				.applyTo(section);
		section.setExpanded(true);
		ScrolledComposite sc = new ScrolledComposite(section, SWT.H_SCROLL
				| SWT.V_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false)
				.applyTo(sc);
		NlputilsFormComposite.addErrorPopup(form.getForm(), toolkit);
		NlputilsFormComposite.createEmptyRow(toolkit, sc);
		Composite client = toolkit.createComposite(form.getBody());
		GridLayoutFactory.fillDefaults().equalWidth(true).numColumns(1)
				.applyTo(client);
		GridDataFactory.fillDefaults().grab(true, false).span(1, 1)
				.applyTo(client);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		// create input data
		inputLayoutData = NlputilsFormComposite.createInputSection(toolkit,
				client, form.getMessageManager());
		Composite compInput;
		// Create pre process link
		compInput = inputLayoutData.getSectionClient();

		seedFileText = createSeedFileControl(compInput, "Seed File Location :",
				"");

		topics = createAdditionalOptions(compInput, "Number of Topics :", "1");

		GridDataFactory.fillDefaults().grab(true, false).span(1, 1)
				.applyTo(compInput);
		createPreprocessLink(compInput);

		Composite client1 = toolkit.createComposite(form.getBody());
		GridLayoutFactory.fillDefaults().equalWidth(true).numColumns(1)
				.applyTo(client1);
		GridDataFactory.fillDefaults().grab(true, false).span(1, 1)
				.applyTo(client1);

		layoutData = NlputilsFormComposite.createOutputSection(toolkit,
				client1, form.getMessageManager());

		// we dont need stop word's as it will be taken from the preprocessor
		// settings

		form.getForm().addMessageHyperlinkListener(new HyperlinkAdapter());
		// form.setMessage("Invalid path", IMessageProvider.ERROR);
		this.setPartName("ZLabel LDA Topic Model");
		addButtonsToToolBar();
		toolkit.paintBordersFor(form.getBody());

	}

	private void createPreprocessLink(Composite client) {

		Composite clientLink = toolkit.createComposite(client);
		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(2)
				.applyTo(clientLink);
		GridDataFactory.fillDefaults().grab(false, false).span(1, 1)
				.applyTo(clientLink);

		preprocessEnabled = toolkit.createButton(clientLink, "", SWT.CHECK);
		GridDataFactory.fillDefaults().grab(false, false).span(1, 1)
				.applyTo(preprocessEnabled);
		final Hyperlink link = toolkit.createHyperlink(clientLink,
				"Preprocess", SWT.NONE);
		link.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		link.addHyperlinkListener(new IHyperlinkListener() {
			public void linkEntered(HyperlinkEvent e) {
			}

			public void linkExited(HyperlinkEvent e) {
			}

			public void linkActivated(HyperlinkEvent e) {
				String id = "edu.usc.cssl.nlputils.common.ui.prepocessorsettings";
				PreferencesUtil.createPreferenceDialogOn(link.getShell(), id,
						new String[] { id }, null).open();
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).span(1, 1)
				.applyTo(link);

	}

	private Text createAdditionalOptions(Composite sectionClient,
			String lblText, String defaultText) {
		Label simpleTxtLbl = toolkit.createLabel(sectionClient, lblText,
				SWT.NONE);
		GridDataFactory.fillDefaults().grab(false, false).span(1, 0)
				.applyTo(simpleTxtLbl);
		Text simpleTxt = toolkit.createText(sectionClient, "", SWT.BORDER);
		simpleTxt.setText(defaultText);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 0)
				.applyTo(simpleTxt);
		return simpleTxt;
	}

	private Text createSeedFileControl(Composite sectionClient, String lblText,
			String defaultText) {
		Label simpleTxtLbl = toolkit.createLabel(sectionClient, lblText,
				SWT.NONE);
		GridDataFactory.fillDefaults().grab(false, false).span(1, 0)
				.applyTo(simpleTxtLbl);
		final Text simpleTxt = toolkit
				.createText(sectionClient, "", SWT.BORDER);
		simpleTxt.setText(defaultText);
		GridDataFactory.fillDefaults().grab(true, false).span(1, 0)
				.applyTo(simpleTxt);
		fAddFileButton = toolkit.createButton(sectionClient, "Browse...",
				SWT.PUSH);
		GridDataFactory.fillDefaults().grab(false, false).span(1, 1)
				.applyTo(fAddFileButton);
		fAddFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(fAddFileButton.getShell(),
						SWT.OPEN);
				dlg.setText("Select File");
				String path = dlg.open();
				if (path == null)
					return;
				simpleTxt.setText(path);
			}
		});

		return simpleTxt;
	}

	private FormToolkit createFormBodySection(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);

		toolkit.decorateFormHeading(form.getForm());
		form.setText("ZLabel LDA Topic Model"); //$NON-NLS-1$
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true)
				.applyTo(form.getBody());
		return toolkit;
	}

	private void addButtonsToToolBar() {
		IToolBarManager mgr = form.getToolBarManager();
		mgr.add(new Action() {
			@Override
			public ImageDescriptor getImageDescriptor() {
				return (ZlabelLdaTopicModelViewImageRegistry
						.getImageIconFactory()
						.getImageDescriptor(IMAGE_LRUN_OBJ));
			}

			@Override
			public String getToolTipText() {
				return "Analyze";
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.action.Action#run()
			 */
			public void run() {
				final int noOfTopics = Integer.valueOf(topics.getText())
						.intValue();
				final boolean isPreprocess = preprocessEnabled.getSelection();
				final String inputPath = inputLayoutData.getOutputLabel()
						.getText();
				final String outputPath = layoutData.getOutputLabel().getText();
				final String seedFilePath = seedFileText.getText();
				job = new Job("Analyzing...") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						NlputilsFormComposite.setConsoleViewInFocus();
						NlputilsFormComposite.updateStatusMessage(
								getViewSite(), null, null, form);
						monitor.beginTask("NLPUtils started analyzing...", 100);
						List<String> inputFiles = new ArrayList<String>();
						String topicModelDirPath = inputPath;
						Preprocess preprocessTask = null;
						if (isPreprocess) {
							monitor.subTask("Preprocessing...");
							preprocessTask = new Preprocess("ZLabelLDA");
							try {
								File[] inputFile = new File(inputPath)
										.listFiles();
								for (File iFile : inputFile) {
									inputFiles.add(iFile.toString());

								}
								topicModelDirPath = preprocessTask
										.doPreprocessing(inputFiles, "");

							} catch (IOException e) {
								e.printStackTrace();
							}
							monitor.worked(10);
						}

						long startTime = System.currentTimeMillis();

						ZlabelTopicModelAnalysis zlda = new ZlabelTopicModelAnalysis(
								new SubProgressMonitor(monitor, 70));
						monitor.subTask("Topic Modelling...");
						zlda.invokeLDA(topicModelDirPath, seedFilePath,
								noOfTopics, outputPath);
						System.out
								.println("ZLabel LDA Topic Modelling completed successfully in "
										+ (System.currentTimeMillis() - startTime)
										+ " milliseconds.");

						if (monitor.isCanceled()) {
							return Status.CANCEL_STATUS;
						}
						monitor.subTask("Cleaning Preprocessed Files...");
						preprocessTask.clean();
						monitor.worked(10);
						monitor.done();
						NlputilsFormComposite.updateStatusMessage(
								getViewSite(),
								"z-Label LDA analysis completed", IStatus.OK, form);
						;

						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
				if (canProceedJob()) {
					job.schedule();
				} else {
					NlputilsFormComposite
							.updateStatusMessage(
									getViewSite(),
									"ZLabel LDA Topic Modelling cannot be started. Please check the Form status to correct the errors",
									IStatus.ERROR, form);
				}

			}

		});
		mgr.add(new Action() {
			@Override
			public ImageDescriptor getImageDescriptor() {
				return (ZlabelLdaTopicModelViewImageRegistry
						.getImageIconFactory()
						.getImageDescriptor(IMAGE_HELP_CO));
			}

			@Override
			public String getToolTipText() {
				return "Help";
			}

			public void run() {

			};
		});
		form.getToolBarManager().update(true);
	}

	@Override
	public void setFocus() {
		form.setFocus();
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == Job.class) {
			return job;
		}
		return super.getAdapter(adapter);
	}

	private boolean canProceedJob() {
		NlputilsFormComposite.updateStatusMessage(getViewSite(), null, null, form);
		boolean canProceed = true;
		form.getMessageManager().removeMessage("location");
		form.getMessageManager().removeMessage("inputlocation");
		form.getMessageManager().removeMessage("topics");
		form.getMessageManager().removeMessage("seedfile");
		String message = OutputPathValidation.getInstance()
				.validateOutputDirectory(layoutData.getOutputLabel().getText(),
						"Output");
		if (message != null) {

			message = layoutData.getOutputLabel().getText() + " " + message;
			form.getMessageManager().addMessage("location", message, null,
					IMessageProvider.ERROR);
			canProceed = false;
		}

		String inputMessage = OutputPathValidation.getInstance()
				.validateOutputDirectory(
						inputLayoutData.getOutputLabel().getText(), "Input");
		if (inputMessage != null) {

			inputMessage = inputLayoutData.getOutputLabel().getText() + " "
					+ inputMessage;
			form.getMessageManager().addMessage("inputlocation", inputMessage,
					null, IMessageProvider.ERROR);
			canProceed = false;
		}

		String seedFileMsg = OutputPathValidation.getInstance()
				.validateOutputDirectory(seedFileText.getText(), "Seed File");
		if (seedFileMsg != null) {

			seedFileMsg = seedFileText.getText() + " " + seedFileMsg;
			form.getMessageManager().addMessage("seedfile", seedFileMsg, null,
					IMessageProvider.ERROR);
			canProceed = false;
		}
		if (Integer.parseInt(topics.getText()) < 1) {
			form.getMessageManager().addMessage("topics",
					"Number of topics cannot be less than 1", null,
					IMessageProvider.ERROR);
			canProceed = false;
		}
		return canProceed;
	}

}