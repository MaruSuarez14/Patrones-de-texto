public class Pattern {

    Box<Component> components;

    Pattern (String stringPat){
        this.components = new Box<>();
        for (int i = 0; i < stringPat.length(); i++) {
            char c =  stringPat.charAt(i);
            //Si el carácter es "%" y es la primera posición del String, el tipo es BEGINOFLINE
            if (c == '%' && i==0) {
                Component component = new Component();
                component.type = Component.tComp.BEGINOFLINE;
                this.components.addElement(component);
            //Si el carácter es "$" y es la última posición del String, el tipo es ENDOFLINE
            } else if(c=='$' && i==stringPat.length()-1){
                Component component = new Component();
                component.type = Component.tComp.ENDOFLINE;
                this.components.addElement(component);
            //Si el carácter es "?", el tipo es QMARK
            } else if (c=='?'){
                Component component = new Component();
                component.type = Component.tComp.QMARK;
                this.components.addElement(component);
            //Si el carácter está entre a y z o A y Z, el tipo es NCHAR
            } else if((c>='a' && c<='z') || (c>='A' && c<='Z')){
                Component component = new Component();
                component.character = c;
                component.type = Component.tComp.NCHAR;
                this.components.addElement(component);
            //Si el carácter es "@", se guarda la siguiente posición del String como tipo NCHAR y saltamos una
            // posición de la i
            } else if(c=='@'){
                char c1 = stringPat.charAt(i+1);
                Component component = new Component();
                component.character = c1;
                component.type = Component.tComp.NCHAR;
                this.components.addElement(component);
                i++;
            //Si el carácter es "[", el tipo es CHARACTERCLASS y su rango será determinado por la función "completeArray"
            //Se saltan las posiciones hasta hallar el siguiente "]".
            } else if (c=='[') {
                Component component = new Component();
                component.type = Component.tComp.CHARACTERCLASS;
                component.range = completeArray(stringPat, i);
                this.components.addElement(component);
                while (stringPat.charAt(i) != ']') {
                    i++;
                }
            //Si el carácter es "+" O "*", es de tipo CLOSURE, su subcomponente es el último componente añadido en el
            //array y se elimina ese último valor del array.
            } else if(c=='*' || c=='+'){
                Component component = new Component();
                component.character = c;
                component.type = Component.tComp.CLOSURE;
                component.subComponent = this.components.get(this.components.size()-1);
                this.components.remove(this.components.size()-1);
                this.components.addElement(component);
            //Para cualquier otro caso, se añade como tipo NCHAR.
            } else {
                Component component = new Component();
                component.character = c;
                component.type = Component.tComp.NCHAR;
                this.components.addElement(component);
            }
        }
    }
    //Función que a partir de la posición del String, completa un array con los valores que hay dentro de los corchetes.
    public char [] completeArray(String stringPat, int i){
        String s = "";
        //Recorre el String (empezando desde la posición recibida por parámetro) y añade los valores que hay en este
        //hasta que se encuentra con un "]".
        for (int j = i+1; j < stringPat.length(); j++) {
            if(stringPat.charAt(j)!=']'){
                s += stringPat.charAt(j);
            } else {
                break;
            }
        }
        //Añadimos cada uno de los valores del String en una posición del array.
        char [] array = new char[s.length()];
        for (int x = 0; x < s.length(); x++) {
            array[x] = s.charAt(x);
        }
        return array;
    }


    @Override
    public String toString(){
        return String.format(this.components.toString());
    }

}
