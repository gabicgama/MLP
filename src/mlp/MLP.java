package mlp;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author gabyc
 */
public class MLP {

    private int qtdIn, qtdh, qtdOut;
    private double[][] Wh, Wo;
    DecimalFormat df = new DecimalFormat("#0.0000");

    public MLP(int qtdIn, int qtdh, int qtdOut) {
        this.qtdIn = qtdIn;
        this.qtdh = qtdh;
        this.qtdOut = qtdOut;

        Wh = new double[qtdIn + 1][qtdh];
        Wo = new double[qtdh + 1][qtdOut];
        Random rnd = new Random();

        //inicia Wh
        for (int i = 0; i < qtdIn + 1; i++) {
            for (int j = 0; j < qtdh; j++) {
                Wh[i][j] = (rnd.nextDouble() * 0.4) - 0.2;
            }
        }
        //inicia Wo
        for (int i = 0; i < qtdh + 1; i++) {
            for (int j = 0; j < qtdOut; j++) {
                Wo[i][j] = (Math.random() * 0.4) - 0.2;//aleatorio
            }
        }

    }

    public double[] treinar(double[] Xin, double[] Y, double ni) {
        double[] ret = new double[2];  
        
        double[] X = new double[qtdIn + 1];
        X[0] = 1;
        //X da posição 1 até fim = Xin;
        for (int i = 1; i < X.length; i++) {
            X[i] = Xin[i - 1];
        }

        double[] H = new double[qtdh + 1];
        H[0] = 1;

        for (int j = 1; j < qtdh + 1; j++) {
            double u = 0;
            for (int i = 0; i < qtdIn + 1; i++) {
                u = u + X[i] * Wh[i][j - 1];//atencao
            }
            H[j] = 1 / (1 + Math.pow(Math.E, -u));
        }

        double[] O = new double[qtdOut];
        double[] DeltaO = new double[qtdOut];

        for (int j = 0; j < qtdOut; j++) {
            double u = 0;
            for (int i = 0; i < qtdh + 1; i++) {
                u = u + H[i] * Wo[i][j];
            }
            O[j] = (1 / (1 + Math.pow(Math.E, -u)));

            DeltaO[j] = O[j] * (1 - O[j]) * (Y[j] - O[j]);
        }

        double[] DeltaH = new double[qtdh + 1];

        for (int i = 1; i < qtdh + 1; i++) {
            double soma = 0;
            for (int j = 0; j < qtdOut; j++) {
                soma += DeltaO[j] * Wo[i][j];
            }
            DeltaH[i] = H[i] * (1 - H[i]) * soma;
        }

        //atualiza Wh
        for (int i = 0; i < qtdIn + 1; i++) {
            for (int j = 0; j < qtdh; j++) {
                Wh[i][j] = Wh[i][j] + (ni * DeltaH[j + 1] * X[i]);//atencao
            }
        }

        //atualiza Wo
        for (int i = 0; i < qtdh + 1; i++) {
            for (int j = 0; j < qtdOut; j++) {
                Wo[i][j] = Wo[i][j] + (ni * DeltaO[j] * H[i]);
            }
        }

        //System.out.println("-> Saida O = : " + df.format(O[0]));
        //imprimeSaida();

        double erroAprox = 0, erroClas = 0, threshold = 0, aux = 0;
        for (int i = 0; i < Wo[0].length; i++) {
            erroAprox += Math.abs(Y[i] - O[i]);

            if (O[i] >= 0.5) {
                threshold = 1;
            } else {
                threshold = 0;
            }
            aux += Math.abs(Y[i] - threshold);
            if (aux > 0) {
                erroClas = 1;
            } else {
                erroClas = 0;
            }
            //erroClas += 0;
        }

        ret[0] = erroAprox;
        ret[1] = erroClas;
        return ret;

        //return O;
    }

    public void imprimeSaida() {
        System.out.println("\n-> Tabela Wh\n");
        System.out.println("H1 \t\tH2");
        for (int i = 0; i < Wh.length; i++) {
            for (int j = 0; j < Wh[0].length; j++) {
                System.out.print("x" + i + " ");
                System.out.print(df.format(Wh[i][j]) + "\t");
            }
            System.out.println("");

        }
        System.out.println("\n-> Tabela Wo\n");
        System.out.println("   O");
        for (int i = 0; i < Wo.length; i++) {
            for (int j = 0; j < Wo[0].length; j++) {
                System.out.print("x" + i + " ");
                System.out.print(df.format(Wo[i][j]) + "\t");
            }
            System.out.println("");

        }
    }

    public static void main(String[] args) {
        // TODO code application logic here

        DecimalFormat df = new DecimalFormat("#0.0000");
        
        File entrada = new File("base.txt");
        Scanner in = null;
        try {
            in = new Scanner(entrada);
        } catch (FileNotFoundException evalor) {
            in = new Scanner(System.in);
        }

        int nAmostras = Integer.parseInt(in.nextLine());
        int numX = Integer.parseInt(in.nextLine());
        int numY = Integer.parseInt(in.nextLine());
        double ni = Double.parseDouble(in.nextLine());
        int epocas = Integer.parseInt(in.nextLine());

        double matX[][] = new double[nAmostras][numX];
        double matY[][] = new double[nAmostras][numY];

        for (int i = 0; i < nAmostras; i++) {
            String linhas = in.nextLine();
            String itens[] = linhas.split(" ");
            for (int j = 0; j < numX; j++) {
                matX[i][j] = Integer.parseInt(itens[j]);
            }
            for (int j = 0; j < numY; j++) {
                matY[i][j] = Integer.parseInt(itens[j + numX]);
            }
        }

        MLP mlp = new MLP(numX, numX, numY);

        for (int i = 0; i < epocas; i++) {

            //System.out.println("\n-- Época " + (i + 1) + " -");
            double[] erroVet = new double[2];
            double erroAprox = 0, erroClas = 0;
            for (int j = 0; j < matX.length; j++) {
                //System.out.println("->Amostra: " + (j + 1));
                double[] x = matX[j];
                double[] y = matY[j];
                erroVet= mlp.treinar(x, y, ni);
                erroAprox += erroVet[0];
                erroClas += erroVet[1];
            }
            System.out.println("Época: "+(i+1)+"\t Erro aproximação: " + df.format(erroAprox) + "\t Erro classificação:  "+erroClas);
        }
    }

}
