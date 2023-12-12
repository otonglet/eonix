package be.eonix.demo;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Circus {

    enum TypeTour {
        ACROBATIE, MUSIQUE
    }

    class Tour {
        private TypeTour type;
        private String nom;

        public Tour(TypeTour type, String nom) {
            this.type = type;
            this.nom = nom;
        }

        public TypeTour getType() {
            return type;
        }

        public String getNom() {
            return nom;
        }
    }

    class Singe {
        private String nom;
        private List<Tour> tours;

        public Singe(String nom, List<Tour> tours) {
            this.nom = nom;
            this.tours = tours;
        }

        public String getNom() {
            return nom;
        }

        public List<Tour> getTours() {
            return tours;
        }
    }

    class Dresseur {
        private Singe singe;

        public Dresseur(Singe singe) {
            this.singe = singe;
        }

        public String getNomSinge() {
            return this.singe.getNom();
        }

        public List<Tour> demanderExecuterTours() {
            return singe.getTours();
        }
    }

    class Spectateur {
        private final Random random = new Random();

        public void reagir(String tourType, String nomTour, String nomSinge) {
            String reaction = random.nextBoolean() ? "siffle" : "applaudit";
            System.out.println("Le spectateur " + reaction + " pendant le tour de " + tourType + " '" + nomTour + "' du singe " + nomSinge);
        }
    }

    @Test
    public void run() {
        Singe coco = new Singe("Coco", Arrays.asList(
                new Tour(TypeTour.ACROBATIE, "triple salto avant"),
                new Tour(TypeTour.MUSIQUE, "Libérée, délivrée"),
                new Tour(TypeTour.ACROBATIE, "triple salto arrière"),
                new Tour(TypeTour.ACROBATIE, "quadruple vrille")));
        Singe abu = new Singe("Abu", Arrays.asList(
                new Tour(TypeTour.ACROBATIE, "trépied"),
                new Tour(TypeTour.MUSIQUE, "La Macarena"),
                new Tour(TypeTour.MUSIQUE, "For whom the bell tolls"),
                new Tour(TypeTour.MUSIQUE, "Another brick in the wall")));

        Spectateur spectateur = new Spectateur();
        Arrays.asList(new Dresseur(coco), new Dresseur(abu))
                .forEach(dresseur -> dresseur.demanderExecuterTours()
                        .forEach(tour -> spectateur.reagir(tour.getType().name().toLowerCase(), tour.getNom(), dresseur.getNomSinge())));
    }

    public static void main(String[] args) {
        new Circus().run();
    }
}
