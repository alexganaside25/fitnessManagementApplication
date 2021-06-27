import entities.Abonat;
import entities.Antrenor;
import enums.ComenziEnum;
import exceptions.NumarInvalidDeTokensException;
import exceptions.ProgresInvalidException;
import sala.PlatformaAutentificare;
import sala.SalaFitness;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    private static final String ABONAT = "abonat";
    private static final String ANTRENOR = "antrenor";
    private static int indexPersoana = 1;

    public static void main(String... args) throws IOException, NumarInvalidDeTokensException, ProgresInvalidException {
        Path inputFile = Paths.get("src/data/input.txt");
        Path outputFile = Paths.get("src/data/out.txt");

        try (BufferedReader reader = Files.newBufferedReader(inputFile);
             BufferedWriter writer = Files.newBufferedWriter(outputFile);

        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(processCommandAndWrite(line) + '\n');
            }
        }
    }

    private static String processCommandAndWrite(String line) throws NumarInvalidDeTokensException, ProgresInvalidException {
        String result = "";
        String[] tokens = line.split("\\s+");
        if (tokens.length == 0) {
            throw new NumarInvalidDeTokensException();
        } else {
            ComenziEnum command = ComenziEnum.getCommand(tokens[0]);
            SalaFitness salaFitness = SalaFitness.getSalaFitness();
            PlatformaAutentificare platformaAutentificare = PlatformaAutentificare.getPlatformaDeAutentificare();
            switch (command) {
                case SIGNUP_ABONAT:
                    int progres = Integer.parseInt(tokens[3]);
                    if (progres < 0 || progres > 10) {
                        throw new ProgresInvalidException();
                    }
                    Abonat abonat = new Abonat(tokens[1], tokens[2], new ArrayList<>(), indexPersoana++, Integer.parseInt(tokens[3]));
                    result = platformaAutentificare.signUpPersoana(abonat, tokens[4], tokens[5]);
                    break;
                case SIGNUP_ANTRENOR:
                    Antrenor antrenor = new Antrenor(tokens[1], tokens[2], new ArrayList<>(), indexPersoana++, Integer.parseInt(tokens[3]));
                    result = platformaAutentificare.signUpPersoana(antrenor, tokens[4], tokens[5]);
                    break;
                case LOGIN_ABONAT:
                    result = platformaAutentificare.loginPersoana(tokens[1], tokens[2], ABONAT);
                    break;
                case LOGIN_ANTRENOR:
                    result = platformaAutentificare.loginPersoana(tokens[1], tokens[2], ANTRENOR);
                    break;
                case LOGOUT_ABONAT:
                    result = platformaAutentificare.logoutPersoana(tokens[1], ABONAT);
                    break;
                case LOGOUT_ANTRENOR:
                    result = platformaAutentificare.logoutPersoana(tokens[1], ANTRENOR);
                    break;
                case INCREMENT_PROGRES:
                    result = salaFitness.incrementProgres(Integer.parseInt(tokens[1]));
                    break;
                case DECREMENT_PROGRES:
                    result = salaFitness.decrementProgres(Integer.parseInt(tokens[1]));
                    break;
                case ADAUGA_ANTRENOR:
                    result = salaFitness.adaugaAntrenor(tokens[1]);
                    break;
                case VIZUALIZARE_ABONATII_MEI:
                    result = salaFitness.vizualizareAbonatiiMei();
                    break;
                case SUBSCRIBE_ABONAT:
                    result = salaFitness.subscribePersoana(ABONAT);
                    break;
                case SUBSCRIBE_ANTRENOR:
                    result = salaFitness.subscribePersoana(ANTRENOR);
                    break;
                case ADAUGA_NEWS:
                    StringBuilder message = new StringBuilder();
                    for (int i = 1; i < tokens.length; ++i) {
                        message.append(tokens[i]).append(" ");
                    }
                    result = salaFitness.adaugaNews(message.toString().replaceFirst(".$", ""));
                    break;
                case INTRA_IN_SALA:
                    result = salaFitness.intraInSala(tokens[1]);
                    break;
                case IESE_DIN_SALA:
                    result = salaFitness.ieseDinSala(tokens[1]);
                    break;
                case VIZUALIZARE_PERSOANE_CU_ANTRENOR:
                    result = salaFitness.vizualizarePersoaneCuAntrenor();
                    break;
                case VIZUALIZARE_ABONATI:
                    result = salaFitness.vizualizareAbonati();
                    break;
                case VIZUALIZARE_ANTRENORI:
                    result = salaFitness.vizualizareAntrenori();
                    break;
                case PERSISTA_ABONATI:
                    result = salaFitness.persistaAbonati();
                    break;
                case PERSISTA_ANTRENORI:
                    result = salaFitness.persistaAntrenori();
                    break;
            }
        }
        return result;
    }
}