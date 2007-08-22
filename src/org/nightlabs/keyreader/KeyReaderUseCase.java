package org.nightlabs.keyreader;

public class KeyReaderUseCase
{
	private String keyReaderID;
	private String name;
	private String description;

	public KeyReaderUseCase(String keyReaderID, String name, String description)
	{
		this.keyReaderID = keyReaderID;
		this.name = name;
		this.description = description;
	}

	public String getKeyReaderID()
	{
		return keyReaderID;
	}
	public String getName()
	{
		return name;
	}
	public String getDescription()
	{
		return description;
	}
}
