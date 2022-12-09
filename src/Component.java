import java.util.Arrays;

public class Component {
    //Carácter del componente
    char character;
    //Tipo de Componente
    enum tComp {NCHAR, ENDOFLINE, BEGINOFLINE, CHARACTERCLASS, CLOSURE, QMARK}
    tComp type;
    //Array del componente
    char [] range;
    //Subcomponente para los componentes de tipos CLOUSURE
    Component subComponent;

    //Constructor vacío
    Component(){}

    @Override
    public String toString(){
        return String.format("Caracter " + this.character + " de tipo " + this.type + " con array "
                + Arrays.toString(this.range) + " y subcomponente " + this.subComponent);
    }


}
