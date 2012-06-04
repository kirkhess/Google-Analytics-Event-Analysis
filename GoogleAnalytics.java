/**
 * Retrieve data from the Google Analytics API. May need some tweaking for your particular system.
 * @author Kirk Hess
 */
public class GoogleAnalytics {

  // Credentials for Client Login Authorization.
  private static final String CLIENT_USERNAME = "xxx";
  private static final String CLIENT_PASS = "xxxx";
  private static final String[] eventDates; //define as needed

  // Table ID constant
  private static final String TABLE_ID = "ga:123456789";

  public static void main(String args[]) {
    try {
      // Service Object to work with the Google Analytics Data Export API.
      AnalyticsService analyticsService = new AnalyticsService("gaExportAPI_acctSample_v2.0");

      // Client Login Authorization.
      analyticsService.setUserCredentials(CLIENT_USERNAME, CLIENT_PASS);

      // Get data from the Account Feed.
      getAccountFeed(analyticsService);

      // Access the Data Feed if the Table Id has been set.
      if (!TABLE_ID.isEmpty()) {

          // Get profile data from the Data Feed.
          getDataFeed(analyticsService);
      }

    } catch (AuthenticationException e) {
      System.err.println("Authentication failed : " + e.getMessage());
      return;
    } catch (IOException e) {
      System.err.println("Network error trying to retrieve feed: " + e.getMessage());
      return;
    } catch (ServiceException e) {
      System.err.println("Analytics API responded with an error message: " + e.getMessage());
      return;
    }
  }

  /**
   * @param {AnalyticsService} Google Analytics service object that
   *     is authorized through Client Login.
   */
  private static void getAccountFeed(AnalyticsService analyticsService)
      throws IOException, MalformedURLException, ServiceException {

    // Construct query from a string.
    URL queryUrl = new URL(
        "https://www.google.com/analytics/feeds/accounts/default?max-results=50");

    // Make request to the API.
    AccountFeed accountFeed = analyticsService.getFeed(queryUrl, AccountFeed.class);

    // Output the data to the screen.
    System.out.println("-------- Account Feed Results --------");
    for (AccountEntry entry : accountFeed.getEntries()) {
      System.out.println(
        "\nAccount Name  = " + entry.getProperty("ga:accountName") +
        "\nProfile Name  = " + entry.getTitle().getPlainText() +
        "\nProfile Id    = " + entry.getProperty("ga:profileId") +
        "\nTable Id      = " + entry.getTableId().getValue());
    }
  }

  /**
   * @param {AnalyticsService} Google Analytics service object that
   *     is authorized through Client Login.
   */
  private static void getDataFeed(AnalyticsService analyticsService, String[] eventDates)
      throws IOException, MalformedURLException, ServiceException {
	StringBuffer skippedRecords = new StringBuffer();
	int skipCount = 0;
	// Create a query using the DataQuery Object.
	for(String eventDate : eventDates){
    DataQuery query = new DataQuery(new URL(
        "https://www.google.com/analytics/feeds/data"));
    query.setStartDate(eventDate);
    query.setEndDate(eventDate);
    query.setDimensions("ga:eventlabel,ga:eventcategory,ga:pagePath");
    query.setMetrics("ga:totalevents,ga:uniqueevents");
    query.setSort("-ga:totalevents");
    query.setMaxResults(500);
    query.setIds(TABLE_ID);
    
    // Make a request to the API.
    DataFeed dataFeed = analyticsService.getFeed(query.getUrl(), DataFeed.class);
  }
}
