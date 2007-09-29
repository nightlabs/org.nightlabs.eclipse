package org.nightlabs.base.config;

import java.util.HashMap;
import java.util.Map;

import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;

public class DialogCfMod
extends ConfigModule
{
	private static final long serialVersionUID = 1L;

	private Map<String, DialogCf> dialogCfs;

	public Map<String, DialogCf> getDialogCfs()
	{
		return dialogCfs;
	}
	public void setDialogCfs(Map<String, DialogCf> dialogCfs)
	{
		this.dialogCfs = dialogCfs;
	}

	@Override
	public void init()
	throws InitException
	{
		super.init();
		if (dialogCfs == null)
			dialogCfs = new HashMap<String, DialogCf>();

		for (DialogCf cf : dialogCfs.values())
			cf._setCfMod(this);
	}

	/**
	 * @param dialogIdentifier A valid identifier.
	 * @return Either <code>null</code> (if no {@link DialogCf} exists) or the instance that has been stored for the given
	 *		<code>dialogIdentifier</code>.
	 */
	public DialogCf getDialogCf(String dialogIdentifier)
	{
		return dialogCfs.get(dialogIdentifier);
	}

	public DialogCf createDialogCf(String dialogIdentifier, int x, int y, int width, int height)
	{
		DialogCf cf = dialogCfs.get(dialogIdentifier);
		if (cf == null) {
			cf = new DialogCf();
			cf._setCfMod(this);
			dialogCfs.put(dialogIdentifier, cf);
			setChanged();
		}

		cf.setX(x);
		cf.setY(y);
		cf.setWidth(width);
		cf.setHeight(height);

		return cf;
	}
}
