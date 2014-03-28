#ifndef BLUETOOTH_H_INCLUDED
#define BLUETOOTH_H_INCLUDED


/****************************************************************************************************/
/*                      BLUETOOTH                                                                 */
/****************************************************************************************************/
void readBluetooth(){

}


/****************************************************************************************************/
/*                      READ BUTTON                                                                 */
/****************************************************************************************************/
void checkButton(){
 int iButtonState = digitalRead(pinBoton);
  if(iButtonState){
    i_num_desplazamientos++;
    digitalWrite(ledPin, HIGH);
  }else{
    digitalWrite(ledPin, LOW);
  }
//  if(b_debug){
//    Serial.print("iButtonState: ");
//    Serial.print(iButtonState, DEC);
//    Serial.print("; numDesp: ");
//    Serial.print(numDesplazamientos, DEC);
//    Serial.print("; iSizeArrayChar: ");
//    Serial.println(i_size_array_characters, DEC);
//  }

  if(i_num_desplazamientos > i_num_max_pixels) i_num_desplazamientos = 0;
}


#endif // BLUETOOTH_H_INCLUDED
