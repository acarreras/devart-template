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

void writeByte2(byte b){
    shiftOut(pinDatos, pinReloj, MSBFIRST, b);
    delayMicroseconds(1);
}


void writeMatrixFilaAFila(){
    int iletra_debug = 0; //El mayor numero siempre es la de la derecha. 0 sería la de la izquierda.
    int numPancartas = 4;
    //Recorro character a character
    for (int numChar=0; numChar<i_size_array_characters-1; numChar++) {  //Lo ¨²ltimo que se escribe ir¨¢ en la columna de la derecha
        for(int tiempoPorLetra=0; tiempoPorLetra<iTimePerLetra; tiempoPorLetra++){
           for(int fila = 0; fila < 7; fila ++)  {
                for(int pancarta=0; pancarta < numPancartas; pancarta++){
                    byte b = ptr_by_frase[numChar+pancarta][fila];
                    if(b_debug){
                        if(pancarta==iletra_debug){
                            writeOneByte(31);
                        }else{
                            writeOneByte(0);
                        }
                    }
                    else{
                        writeOneByte(b);
                    }
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


#endif // UTILS_H_INCLUDED