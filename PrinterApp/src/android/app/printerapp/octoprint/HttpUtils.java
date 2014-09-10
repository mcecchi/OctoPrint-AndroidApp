package android.app.printerapp.octoprint;

/**
 * Addresses and static fields for the OctoPrint API connection
 * @author alberto-baeza
 *
 */

public class HttpUtils {
	
	  public static final String CUSTOM_PORT = ":5000"; //Octoprint server listening port
	  public static final String API_KEY = "5A41D8EC149F406F9F222DCF93304B43"; //Hardcoded API Key
	  
	  /** OctoPrint URLs **/
	  
	  public static final String URL_FILES = CUSTOM_PORT + "/api/files"; //File operations
	  public static final String URL_CONTROL = CUSTOM_PORT + "/api/job"; //Job operations
	  public static final String URL_SOCKET = CUSTOM_PORT + "/sockjs/websocket"; //Socket handling
	  public static final String URL_CONNECTION = CUSTOM_PORT + "/api/connection"; //Connection handling

}
