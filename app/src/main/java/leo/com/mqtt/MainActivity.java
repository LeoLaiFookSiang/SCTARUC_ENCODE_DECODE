package leo.com.mqtt;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import java.math.BigInteger;

public class MainActivity extends AppCompatActivity {

    EditText editTextName,editTextYear,editTextPassword;
    RadioButton radMale,radFemale;
    Spinner spinner;
    TextView txtViewEncodeResult;
    static int decodeCurrentCursor = 0; // To check current cursor, every cursor checked, will increase the number check

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextName = (EditText) findViewById(R.id.editTextName);
        radMale = (RadioButton) findViewById(R.id.radMale);
        radFemale = (RadioButton) findViewById(R.id.radFemale);
        spinner = (Spinner) findViewById(R.id.spinnerProgramme);
        editTextYear = (EditText) findViewById(R.id.editTextYear);
        editTextPassword = (EditText) findViewById(R.id.editTextPass);
        txtViewEncodeResult = (TextView) findViewById(R.id.txtViewEncodeResult);

    }

    public void btnDecode(View v) {
        decodeCurrentCursor = 0;

        //                 Prepare message publish to MQTT Server
        //===============================================================================
        // 0001 = Login Action
        // 0000 = Researve
        // 124c6565205761682050656e67 = 12 Length of Username (First 2 ASCll character)
        //                            = 4c6565205761682050656e67 Username
        // 066c656f313233 = 06 Length of password (First 2 ASCll character)
        //                = 6c656f313233 Password
        String sendSubscibeData = "00010000124c6565205761682050656e67066c656f313233";

        // Alert show what data publish to server
        AlertDialog.Builder publishBuilder = new AlertDialog.Builder(this);
        publishBuilder.setMessage("Data publish: " + sendSubscibeData)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog publishAlert = publishBuilder.create();
        publishAlert.show();

        //           Perform publish and receive publish from MQTT Server
        //================================================================================

        // Here to perform publish data to MQTT server and receive data publish
        // from MQTT server (Code by yourself)

        //=================================================================================


        //             Receive data publish from MQTT Server and split it
        //=================================================================================
        // 0005 = Respond from server
        // 0000 = Reserve
        // 124c6565205761682050656e67 = 12 Length of Username (First 2 ASCll character)
        //                            = 4c6565205761682050656e67 Username
        //
        // 000002 = Female (Gender)
        //
        // 525344 = RSD (Programme Code)
        //
        // 000003 = 3 (Year of study)
        //
        // 066c656f313233 = 06 Length of password (First 2 ASCll character)
        //                = 6c656f313233 Password
        String receiveSubscribeData = "00050000124c6565205761682050656e67000002525344000003066c656f313233"; // Sample receive data from MQTT Server
        if(receiveSubscribeData.length()%2 == 0){
            if(receiveSubscribeData.charAt(decodeCurrentCursor+3) == '5'){ // Check if the server return command 5 = Login Respond

                decodeCurrentCursor += 7;

                char nameLengthFirstDigit = receiveSubscribeData.charAt(decodeCurrentCursor + 1);
                char nameLengthSecondDigit = receiveSubscribeData.charAt(decodeCurrentCursor + 2);
                decodeCurrentCursor += 2;
                int nameSize = Integer.parseInt(String.valueOf(nameLengthFirstDigit)+String.valueOf(nameLengthSecondDigit));
                editTextName.setText(hexToAscii(splitString(nameSize,receiveSubscribeData)));

                if(receiveSubscribeData.charAt(decodeCurrentCursor + 6) == '1'){
                    radMale.setChecked(true);
                }else if(receiveSubscribeData.charAt(decodeCurrentCursor + 6) == '2'){
                    radFemale.setChecked(true);
                }else{

                }
                decodeCurrentCursor += 6;

                String programmeCode = hexToAscii(splitString(3,receiveSubscribeData)); //Decode from hex to ASCll String
                if(programmeCode.equals("RSD")){
                    spinner.setSelection(0);
                }else if(programmeCode.equals("RIT")){
                    spinner.setSelection(1);
                }

                editTextYear.setText(receiveSubscribeData.charAt(decodeCurrentCursor + 6)+"");
                decodeCurrentCursor += 6;

                char passwordLengthFirstDigit = receiveSubscribeData.charAt(decodeCurrentCursor+1);
                char passwordLengthSecondDigit = receiveSubscribeData.charAt(decodeCurrentCursor+2);
                decodeCurrentCursor += 2;
                int passwordSize = Integer.parseInt(String.valueOf(passwordLengthFirstDigit)+String.valueOf(passwordLengthSecondDigit));
                editTextPassword.setText(hexToAscii(splitString(passwordSize,receiveSubscribeData)));
            }else{

                publishBuilder.setMessage("Receive publish")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                publishAlert.show();
            }


        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Data is not completed")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void btnEncode(View v) {
        decodeCurrentCursor = 0;
        String encodeText = "00060000"; // <-- Action code 0006, reserve code 0000.
        String name = editTextName.getText().toString();
        String password = editTextPassword.getText().toString();
        String gender = "";
        if(radMale.isChecked()){
            gender = "000001";
        }else if(radFemale.isChecked()){
            gender = "000002";
        }
        String programmeCode = spinner.getSelectedItem().toString();
        String numOfYear = "00000" + editTextYear.getText().toString();

        //Turn ASCll name, programme code and passsword to HEX.
        encodeText += name.length() + asciiToHex(name) + gender + asciiToHex(programmeCode) + numOfYear
                + password.length() + asciiToHex(password);

        txtViewEncodeResult.setText("Encode Result: \n" + encodeText);

        //Turn whole ASCll String to byte (If necessary)
        byte[] b = new BigInteger(encodeText,16).toByteArray();

        for(int i=0; i < b.length; i++){
            Log.d("Byte" + i, b[i] + "");
        }


    }

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
