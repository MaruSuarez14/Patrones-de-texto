public class Find {

    private final String text;
    private int countMatches = 0;

    public Find(String text) {
        this.text = text;
    }

    public boolean match(String stringPat) {
        Pattern pattern = new Pattern(stringPat);
        for (int i = 0; i < this.text.length(); i++) {
            if (pattern.components.size() > 0) {
                Component firstComponent = (Component) pattern.components.get(0);
                //Si el primer carácter del patron coincide con el primer carácter del texto, entramos en la función
                //isMatching i--> i+1, j --> 1 (saltamos una posición que ya hemos evaluado)
                if (this.text.charAt(i) == firstComponent.character || firstComponent.type == Component.tComp.QMARK) {
                    this.countMatches = 1;
                    if(textMatch(pattern, i+1, 1)) return true;
                //Si el primer componente es de tipo BEGINOFLINE, entramos en la función isMatching
                // i--> i, j --> 1 (saltamos la posición j de pattern porque no la queremos comparar)
                } else if (firstComponent.type == Component.tComp.BEGINOFLINE && i==0) {
                    this.countMatches = 1;
                    if (textMatch(pattern, i, 1)) return true;
                //Si el primer componente es de tipo CHARACTERCLASS o CLOUSURE entramos en la función isMatching
                // i--> i, j --> 0 (no saltamos ninguna posición)
                } else if(firstComponent.type  == Component.tComp.CHARACTERCLASS ||
                        firstComponent.type == Component.tComp.CLOSURE){
                    this.countMatches = 0;
                    if (textMatch(pattern, i, 0)) return true;
                }
            } else {
                return false;
            }
       }
        return false;
    }

    public boolean textMatch(Pattern pattern, int textPos, int patternPos){
            if (pattern.components.size() > 0) {
                for (; patternPos < pattern.components.size(); patternPos++) {
                    if(textPos<this.text.length()) {
                        Component actualComponent = (Component) pattern.components.get(patternPos);
                        //Para los tipos NCHAR, QUARK y CHARACTERCLASS, llamamos a la función componentMatch, y si es
                        //true, aumentaremos en 1 el contador de coincidencias
                        if (actualComponent.type == Component.tComp.NCHAR || actualComponent.type == Component.tComp.QMARK ||
                                actualComponent.type == Component.tComp.CHARACTERCLASS) {
                            if (componentMatch(actualComponent, textPos)) this.countMatches++;
                        //Para los tipos CLOSURE, llamamos a la función closureMatch y guardamos en las variables
                        //patternPos y textPos los valores devueltos de dicha función
                        } else if (actualComponent.type == Component.tComp.CLOSURE) {
                            int [] iterations = closureMatch(actualComponent, pattern, textPos, patternPos);
                            textPos = iterations[0];
                            patternPos = iterations[1];
                        }
                    }  else {
                        //Cuando el texto ha llegado a su fin, comprobamos que el componente actual sea de tipo ENDOFLINE
                        //y si lo es y además la textPos coincide con el final del texto, se suma uno al contador
                        Component actualComponent = (Component) pattern.components.get(patternPos);
                        if (actualComponent.type == Component.tComp.ENDOFLINE && textPos == this.text.length()) {
                            this.countMatches++;
                        }
                    }
                    textPos++;
                }
            } else {
                return false;
            }
        //Si el número de countMatches es igual al número de componentes, la función devolverá true
        return this.countMatches == pattern.components.size();
    }

    public int [] closureMatch(Component actualComponent, Pattern pattern, int textPos, int patternPos){
        boolean inWhile = false;
        Component subcomponent = actualComponent.subComponent;
        int oldTextPos = textPos;
        //Mientras que el subcomponente coincida con el carácter de la siguiente posición del texto, sumamos una posición
        while (componentMatch(subcomponent, textPos)) {
            inWhile = true;
            textPos++;
        }
        //Si no se ha entrado en el bucle pero el carácter es *, se suma uno a countMatches
        if (actualComponent.character == '*' && !inWhile) this.countMatches++;
        //Si entramos en el bucle, se suma uno a countMatches
        else if (inWhile) this.countMatches++;
        //Se vuelve a guardar la actual posición del texto (después de entrar en el bucle)
        int actualTextPos = textPos;
        boolean reverseMatch = false;
        //Mientras que no vuelva a la posición que estaba antes de entrar en el bucle, se ejecutará este bucle que
        //se encargará en ir hacia atrás
        while (oldTextPos!=textPos){
            if (patternPos < pattern.components.size() - 1 && textPos > 0) {
                Component nextComponent = (Component) pattern.components.get(patternPos + 1);
                //Si el siguiente componente coincide con la posición del texto anterior, se suma uno a countMatches
                //y a la posición del patrón
                if (componentMatch(nextComponent, textPos - 1)) {
                    this.countMatches++;
                    reverseMatch = true;
                    patternPos++;
                }
            }
            textPos--;
        }
        //Si no se ha encontrado ninguna coincidencia, revertimos la modificación de la variable textPos
        if(!reverseMatch) textPos = actualTextPos;
        //Si el subcomponente no ha hecho match con el carácter de la última posición, se resta uno a la posición del texto
        if(!componentMatch(subcomponent, textPos)){
            textPos--;
        }
        return new int[]{textPos, patternPos};
    }


    //Esta función recibe un componente y una posición del String text y devuelve true si los caracteres son iguales
    public boolean componentMatch(Component component, int textPos){
        //Comprobación para saber que no nos encontramos en el final del texto
        if(textPos<this.text.length()){
            //Si el carácter del componente es igual al carácter de la posición textPos del texto, o el componente es de
            //tipo QMARK, se devuelve true
            if (this.text.charAt(textPos) == component.character || component.type == Component.tComp.QMARK) {
                return true;
            //Si el componente es de tipo CHARACTERCLASS, se hará uso de su atributo rango
            } else if(component.type == Component.tComp.CHARACTERCLASS){
                //Bucle para los casos en que el array contiene un "-"
                for (int j = 0; j < component.range.length; j++) {
                    if(component.range[j]=='-'){
                        //Guardamos el carácter a la izquierda y derecha del guion
                        int initialPosition = component.range[j-1];
                        int finalPosition = component.range[j+1];
                        //Si el carácter se encuentra entre el rango determinado, se devuelve true
                        if(this.text.charAt(textPos) >= initialPosition && this.text.charAt(textPos) <= finalPosition){
                            return true;
                        }
                    }
                }
                //Bucle para los casos en el que el array no contiene un "-"
                for (int j = 0; j < component.range.length; j++) {
                    //Si el carácter de la posición actual del texto coincide con alguno de los que se encuentran en el
                    //array, se devuelve true.
                    if(this.text.charAt(textPos)==component.range[j]) return true;
                }
            }
        }
        return false;
    }

    public String capture(String stringPat) {
        return "";
    }


}
