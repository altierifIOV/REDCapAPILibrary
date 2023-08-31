package it.ioveneto.redcap.test;

import it.ioveneto.redcap.api.ExportProject;

/**
 * Main class with API invocation example
 */

public class Main
{
	/**
	 * just displays out some basic project info in json format
	 * @param args 0 - API token, 1 - your REDCap API endpoint (typical https://myredcapurl/redcap/api/)
	 */
	public static void main(final String[] args)
	{
		final ExportProject exportProjectSource = new ExportProject(args[0], "json", args[1]);
		exportProjectSource.doPost();

	}
}
