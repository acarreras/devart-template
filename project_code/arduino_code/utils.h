#ifndef UTILS_H_INCLUDED
#define UTILS_H_INCLUDED



/****************************************************************************************************
                      CHAR -> PHRASE
****************************************************************************************************/

void createAsciiArray(){
  for(int i=0; i<i_size_array_characters; i++){
    ptr_by_ascii_char[i] = int(original_characters[i])-32;
  }
}
void createPhraseArray(){
  for (int u=0; u<i_size_array_characters; u++) { //Number of characters
    for (int v=6; v>=0; v--) { //Number of rows in the matrix
      ptr_by_frase[u][v] = led_font_list [ptr_by_ascii_char[u]] [v];
    }//for v;
  }//for u;
}

/****************************************************************************************************
                      WRITE INTO THE MATRIXES
/****************************************************************************************************/
void writeBitData(int data){
  digitalWrite(pinReloj, 0);
  digitalWrite(pinDatos, data);
  digitalWrite(pinReloj, 1);

  if(b_mode_cloth)digitalWrite(pinDatos, 1);
  else digitalWrite(pinDatos, 0);

  digitalWrite(pinReloj, 0);
}


void writeOneRow(byte * ptr_byte_to_send){

  int auxiliar = 0;
  int numCharacter = 0;
  int numPixel = 0;
  int data = 0;

  if(b_mode_cloth)data = 1;
  else data = 0;

  for(int desp=0; desp<i_num_desplazamientos; desp++){
     if (ptr_byte_to_send[numCharacter] & (1<<(i_num_pixels_per_matrix+i_pixels_between_chars-numPixel-1))){
          if(b_mode_cloth)data = 0;
          else data = 1;
      }
      else{
          if(b_mode_cloth)data = 1;
          else data = 0;
      }

      writeBitData(data);

      //These lines are to control which character is being introduced
      if(numPixel < (i_num_pixels_per_matrix+i_pixels_between_chars-1)){
         numPixel++;
      }
      else {
        numCharacter++;
        numPixel = 0;
      }
      if(numCharacter < i_size_array_characters) {
        ;
      }else{
        numCharacter = 0;
      }
  }

}

void writeBitDataAndWait(int data){

  digitalWrite(pinDatos, data);
  digitalWrite(pinReloj, 0);
  delayMicroseconds(1);
  digitalWrite(pinReloj, 1);
  delayMicroseconds(1);

//  if(b_mode_cloth)digitalWrite(pinDatos, 1);
//  else digitalWrite(pinDatos, 0);
//
//  digitalWrite(pinReloj, 0);

//  //remove
//  if(b_mode_cloth)digitalWrite(pinDatos, 0);
//  else digitalWrite(pinDatos, 1);
}

// 1 frase = n X m letras
// 1 letra = 7(filas) x 1 byte
void writeOneByte(byte b){
  int data = 0;

  if(b_mode_cloth)data = 1;
  else data = 0;

  //for(int desp=0; desp<i_num_desplazamientos; desp++){
  //Cojo cada bit para 5 columnas
  for(int desp=4; desp>=0; desp--){
     if (b & (1<<desp)){
          if(b_mode_cloth)data = 0;
          else data = 1;
      }
      else{
          if(b_mode_cloth)data = 1;
          else data = 0;
      }
      writeBitDataAndWait(data);
      //writeBitData(data);

  }
}
/**
    Este método va letra a letra, y por cada una la descompone en bytes
    y escribe las 7 filas. Después mete un nuevo caracter.
*/
void writeRowByte(int fila, byte b){
    if(b_mode_cloth)digitalWrite(iArray_pins_filas[fila], HIGH); //ENCIENDE FILA 1
    else digitalWrite(iArray_pins_filas[fila], LOW); //ENCIENDE FILA 1
    for(int i=0; i<30; i++){
        shiftOut(pinDatos, pinReloj, MSBFIRST, b);
        delayMicroseconds(1);
    }
    if(b_mode_cloth)digitalWrite(iArray_pins_filas[fila], HIGH); //ENCIENDE FILA 1
    else digitalWrite(iArray_pins_filas[fila], LOW); //ENCIENDE FILA 1
}
void writeByte2(byte b){
    shiftOut(pinDatos, pinReloj, MSBFIRST, b);
    delayMicroseconds(1);
}


void writeMatrixFilaAFila(){
    int numPancartas = 6;
    //Recorro character a character
    for (int numChar=0; numChar<i_size_array_characters-1; numChar++) {  //Lo ¨²ltimo que se escribe ir¨¢ en la columna de la derecha
        for(int tiempoPorLetra=0; tiempoPorLetra<100; tiempoPorLetra++){
           for(int fila = 0; fila < 7; fila ++)  {
                for(int i=0; i < numPancartas; i++){
                    byte b = ptr_by_frase[numChar+i][fila];
                    writeOneByte(b);
//                    if(b_mode_cloth){
//                        writeOneByte(b);
//                    }else{
//                        writeByte2(b);
//                    }
                }
                if(b_mode_cloth)digitalWrite(iArray_pins_filas[fila], HIGH); //ENCIENDE FILA 1
                else digitalWrite(iArray_pins_filas[fila], LOW); //ENCIENDE FILA 1
                delay(1);
                if(b_mode_cloth)digitalWrite(iArray_pins_filas[fila], LOW); //APAGA
                else digitalWrite(iArray_pins_filas[fila], HIGH); //APAGA
            }

            }
        }
//delay(idelayBetweenChars);
}
void writeMatrixLetraALetraProgresivo(){
    int ciclos = 10;
    int idelay = 1;
    int idelayBetweenChars = 100;
    int tMax = 35;

    //Recorro character a character
//    for (int numChar=0; numChar<i_size_array_characters; numChar++) {  //Lo ¨²ltimo que se escribe ir¨¢ en la columna de la derecha
        //Para cada caracter creo un array de 7 bytes (uno por fila);
        for(int t=0; t<tMax; t++){
            for(int fila = 0; fila < 7; fila ++)  {
                //byte b = ptr_by_frase[numChar][fila];
                //Escribo el byte en cada fila
                if(b_mode_cloth)digitalWrite(iArray_pins_filas[fila], HIGH); //ENCIENDE FILA 1
                else digitalWrite(iArray_pins_filas[fila], LOW); //ENCIENDE FILA 1
                    for(int i=0; i<ciclos; i++)
                        writeOneByte(1);
                    delay(idelay);

                if(b_mode_cloth)digitalWrite(iArray_pins_filas[fila], LOW); //APAGA
                else digitalWrite(iArray_pins_filas[fila], HIGH); //APAGA
            }
            delay(1000);
        }
            //delay(idelayBetweenChars);
//    }
}
/**
    Este método va fila por fila y en cada una mete todos los bytes correspondientes
    a una fila completa (es decir, si tenemos 10 letras, pues 10 bytes a la vez para una fila.
*/

void writeMatrix(){
  for(int fila = 0; fila < 7; fila ++)  {
     /* Carga los bytes apropiados con espacios a mandar teniendo en cuenta el numero de caracteres */
    for (int numChar=0; numChar<i_size_array_characters; numChar++) {  //Lo ¨²ltimo que se escribe ir¨¢ en la columna de la derecha!
      ptr_by_byte_to_send_per_row[numChar] = (0 << i_num_pixels_per_matrix) | ptr_by_frase[numChar][fila];
      //ptr_by_byte_to_send_per_row[numChar] = ptr_by_frase[numChar][fila];
    }
    /* Maneja encendido y apagado de las filas */
    if(b_mode_cloth)digitalWrite(iArray_pins_filas[fila], HIGH); //ENCIENDE FILA 1
    else digitalWrite(iArray_pins_filas[fila], LOW); //ENCIENDE FILA 1

      writeOneRow(ptr_by_byte_to_send_per_row);
      delay(3); //4

    if(b_mode_cloth)digitalWrite(iArray_pins_filas[fila], LOW); //APAGA
    else digitalWrite(iArray_pins_filas[fila], HIGH); //APAGA

  }

  if(b_debug){
    Serial.print("numDesp: ");
    Serial.println(i_num_desplazamientos, DEC);
  }
}


#endif // UTILS_H_INCLUDED
