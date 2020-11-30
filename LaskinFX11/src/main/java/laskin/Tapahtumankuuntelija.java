package laskin;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;


public class Tapahtumankuuntelija implements EventHandler {
    private Button undo;
    private Sovelluslogiikka sovellus;

    private Map<Button, Komento> komennot;
    private Komento edellinen = null;


    public Tapahtumankuuntelija(TextField tuloskentta, TextField syotekentta, Button plus, Button miinus, Button nollaa, Button undo) {
        this.undo = undo;
        this.sovellus = new Sovelluslogiikka();
        this.komennot = new HashMap<>();
        this.komennot.put(plus, new Summa(tuloskentta, syotekentta, nollaa, undo, sovellus));
        this.komennot.put(miinus, new Erotus(tuloskentta, syotekentta, nollaa, undo, sovellus));
        this.komennot.put(nollaa, new Nollaa(tuloskentta, syotekentta, nollaa, undo, sovellus));
    }

    @Override
    public void handle(Event event) {
        if (event.getTarget() != undo) {
            Komento komento = this.komennot.get((Button) event.getTarget());
            komento.suorita();
            this.edellinen = komento;
        } else {
            this.edellinen.peru();
            this.edellinen = null;
        }
    }

}


abstract class Komento {
    protected TextField tulos, syote;
    protected Button nollaa, undo;
    protected Sovelluslogiikka sovellus;

    public Komento(TextField tulos, TextField syote, Button nollaa, Button undo, Sovelluslogiikka sovellus) {
        this.tulos = tulos;
        this.syote = syote;
        this.nollaa = nollaa;
        this.undo = undo;
        this.sovellus = sovellus;
    }

    public abstract void suorita();

    public abstract void peru();
    
    public int haeArvo(TextField kentta) {

        try {
            return Integer.parseInt(syote.getText());
        } catch (Exception e) {
            return 0;
        }
    }
    
    public void paivita() {

        int laskunTulos = sovellus.tulos();

        syote.setText("");
        tulos.setText("" + laskunTulos);

        if ( laskunTulos==0) {
            nollaa.disableProperty().set(true);
        } else {
            nollaa.disableProperty().set(false);
        }
        undo.disableProperty().set(false);
    }
}


class Summa extends Komento {
    
    private int syoteArvo;

    public Summa(TextField tulos, TextField syote, Button nollaa, Button undo, Sovelluslogiikka sovellus) {
        super(tulos, syote, nollaa, undo, sovellus);
    }

    @Override
    public void suorita() {
        int arvo = haeArvo(syote);
        sovellus.plus(arvo);
        syoteArvo = arvo;
        paivita();
    }

    @Override
    public void peru() {
        sovellus.miinus(syoteArvo);
        paivita();
    }
}

class Erotus extends Komento {

    private int syoteArvo;
    public Erotus(TextField tulos, TextField syote, Button nollaa, Button undo, Sovelluslogiikka sovellus) {
        super(tulos, syote, nollaa, undo, sovellus);
    }

    @Override
    public void suorita() {
        int arvo = haeArvo(syote);
        sovellus.miinus(arvo);
        syoteArvo = arvo;
        paivita();
    }


    @Override
    public void peru() {
        sovellus.plus(syoteArvo);
        paivita();
    }
}
class Nollaa extends Komento {

    private int edellinen;
    
    public Nollaa(TextField tulos, TextField syote, Button nollaa, Button undo, Sovelluslogiikka sovellus) {
        super(tulos, syote, nollaa, undo, sovellus);
    }

    @Override
    public void suorita() {
        edellinen = sovellus.tulos();
        sovellus.nollaa();
        paivita();
    }

    @Override
    public void peru() {
        sovellus.plus(edellinen);    
        paivita();
    }
}
