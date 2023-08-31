package it.ioveneto.redcap.test;

import it.ioveneto.redcap.api.ExportProject;

/**
 * Main class with API invocations example
 */

public class Main
{
	public static void main(final String[] args)
	{

		final ExportProject exportProjectSource = new ExportProject("myAPIToken", "json", "myRedcapAPIURL");
		exportProjectSource.doPost();

		//final ImportRecordsJSON importRecords = new ImportRecordsJSON(c);
		//importRecords.doPost();

		//final ImportRecordsJSON importRecords = new ImportRecordsJSON(c);
		//importRecords.doPost();

		//final ImportProjectJSON importProject = new ImportProjectJSON(c);
		//importProject.doPost();

		//final ImportInstrumentEventMapsJSON importInstrumentEventMaps = new ImportInstrumentEventMapsJSON(c);
		//importInstrumentEventMaps.doPost();

		//final ImportUserRights importUserRights = new ImportUserRights(c);
		//importUserRights.doPost();

		//final DeleteEvents deleteEvents = new DeleteEvents(c);
		//deleteEvents.doPost();

		//final DeleteArms deleteArms = new DeleteArms(c);
		//deleteArms.doPost();

		//final ImportEventsJSON importEvents = new ImportEventsJSON(c);
		//importEvents.doPost();

		//final ImportArms importArms = new ImportArms(c);
		//importArms.doPost();

		//final ExportArms exportArms = new ExportArms(c);
		//exportArms.doPost();

		//final ExportFile exportFile = new ExportFile(c);
		//exportFile.doPost();

		//final ExportInstrumentsPDF exportInstrumentsPDF = new ExportInstrumentsPDF(c);
		//exportInstrumentsPDF.doPost();

		//final DeleteFile deleteFile = new DeleteFile(c);
		//deleteFile.doPost();

		//final ImportFile importFile = new ImportFile(c);
		//importFile.doPost();

		//final ImportRecordsJSON importRecords = new ImportRecordsJSON(c);
		//importRecords.doPost();

		//final ExportUsers exportUsers = new ExportUsers(c);
		//exportUsers.doPost();

		//final ExportSurveyReturnCode exportSurveyReturnCode = new ExportSurveyReturnCode(c);
		//exportSurveyReturnCode.doPost();

		//final ExportSurveyQueueLink exportSurveyQueueLink = new ExportSurveyQueueLink(c);
		//exportSurveyQueueLink.doPost();

		//final ExportSurveyParticipants exportSurveyParticipants = new ExportSurveyParticipants(c);
		//exportSurveyParticipants.doPost();

		//final ExportSurveyLink exportSurveyLink = new ExportSurveyLink(c);
		//exportSurveyLink.doPost();

		//final ExportReports exportReports = new ExportReports(c);
		//exportReports.doPost();

		//final ExportREDCapVersion exportREDCapVersion = new ExportREDCapVersion(c);
		//exportREDCapVersion.doPost();

		//final ExportRecords exportRecords = new ExportRecords(c);
		//exportRecords.doPost();

		//final ExportMetatdata exportMetatdata = new ExportMetatdata(c);
		//exportMetatdata.doPost();

		//final ExportInstrumentEventMaps exportInstrumentEventMaps = new ExportInstrumentEventMaps(c);
		//exportInstrumentEventMaps.doPost();

		//final ExportInstruments exportInstrument = new ExportInstruments(c);
		//exportInstrument.doPost();

		//final ExportFieldNames exportFieldNames = new ExportFieldNames(c);
		//exportFieldNames.doPost();

		//final ExportEvents exportEvents = new ExportEvents(c);
		//exportEvents.doPost();
	}
}
