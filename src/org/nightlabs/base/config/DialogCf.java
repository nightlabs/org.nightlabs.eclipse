package org.nightlabs.base.config;

public class DialogCf
{
	private DialogCfMod cfMod;

	public DialogCfMod _getCfMod()
	{
		return cfMod;
	}
	public void _setCfMod(DialogCfMod cfMod)
	{
		this.cfMod = cfMod;
	}

	private int x;
	private int y;
	private int width;
	private int height;

	public int getX()
	{
		return x;
	}
	public void setX(int x)
	{
		if (this.x != x && cfMod != null) {
			this.x = x;
			cfMod.setChanged();
		}
		else
			this.x = x;
	}
	public int getY()
	{
		return y;
	}
	public void setY(int y)
	{
		if (this.y != y && cfMod != null) {
			this.y = y;
			cfMod.setChanged();
		}
		else
			this.y = y;
	}
	public int getWidth()
	{
		return width;
	}
	public void setWidth(int width)
	{
		if (this.width != width && cfMod != null) {
			this.width = width;
			cfMod.setChanged();
		}
		else
			this.width = width;
	}
	public int getHeight()
	{
		return height;
	}
	public void setHeight(int height)
	{
		if (this.height != height && cfMod != null) {
			this.height = height;
			cfMod.setChanged();
		}
		else
			this.height = height;
	}
}