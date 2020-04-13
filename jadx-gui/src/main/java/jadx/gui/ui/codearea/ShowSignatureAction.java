package jadx.gui.ui.codearea;

import jadx.gui.treemodel.JNode;
import jadx.gui.ui.SignatureDialog;
import jadx.gui.ui.UsageDialog;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;

public final class ShowSignatureAction extends JNodeMenuAction<JNode> {
	private static final long serialVersionUID = 4692546569977976384L;
	private static final Logger LOG = LoggerFactory.getLogger(ShowSignatureAction.class);

	public ShowSignatureAction(CodeArea codeArea) {
		super("显示签名", codeArea);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (node == null) {
			return;
		}

		SignatureDialog dialog = new SignatureDialog(codeArea.getMainWindow(), node);
		dialog.setVisible(true);
	}

	@Nullable
	@Override
	public JNode getNodeByOffset(int offset) {
		return codeArea.getJNodeAtOffset(offset);
	}
}
