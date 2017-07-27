package leo.com.mqtt;

import static leo.com.mqtt.MainActivity.decodeCurrentCursor; // Import static cursor from Main Activity

/**
 * Created by User on 27/7/2017.
 */

public class Action {
    //This function perform split string, parameter is size of String and String data
    public String splitString(int length, String subscribeData){
        String completeString = "";
        for(int i = 0 ; i < length*2 ; i++){ // Length *2 because 1 byte have 2 bit
            decodeCurrentCursor ++;          // Every loop cursor increase 1
            completeString += subscribeData.charAt(decodeCurrentCursor); // Store every char split out into string
        }
        return completeString;
    }

    //Turn Hex to ASCll For example : 31 turn to 1
    public String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    //Turn ASCll to Hex For example : 1 turn to 31
    public String asciiToHex(String asciiStr) {
        char[] chars = asciiStr.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char ch : chars) {
            hex.append(Integer.toHexString((int) ch));
        }

        return hex.toString();
    }
}
