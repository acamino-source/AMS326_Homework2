import java.util.Scanner;
import java.util.Random;
public class matmult {
    //File: matmult.java
    //Purpose: This is the main method runs the code and prints out the
    //         final matrices as well as number of additions and multiplications for each Algorithm.
    //
    //Input: a: integer value of the dimensions of the two square matrices you wish to multiply
    //          for example: a 2^(10) x 2^(10) matrix would be entered as 1024 (= 2^(10))
    //
    //Output: estimate of the number of multiplications and additions performed
    //        as well as a print out of the resulting matrices by using the naive and Strassen
    //        algorithm
    //
    //
    //Compile: javac matmult.java
    //Execute: java matmult
    //
    //
    //Notes:    FOR SIMPLICITY*** printing of matrix was commented out since the runtime of this
    //          program was a recurring issue.

    static public int counterAdd = 0;
    static public int counterMult = 0;
    
    public static void main(String[] args){
	int size;
	Scanner sc = new Scanner(System.in);
	System.out.println("Enter dimension of square matrices (as an integer)");
	size = sc.nextInt();
        double[][] a = matrixBuild(size);
        double[][] b = matrixBuild(size);
        double[][] naive_matrix = NaiveAlgorithm(a, b);
        /*printMatrix(naive_matrix);*/
        double[][] strassen_matrix = strassenAlgorithm(a, b);
        /*printMatrix(strassen_matrix);*/
        System.out.println("The number of Strassen additions was: " + counterAdd);
        System.out.println("The number of Strassen multiplications was: " + counterMult);
    }

    public static double[][] matrixBuild(int size){
        double[][] matrix = new double[size][size];;
        for(int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[0].length; col++) {
                matrix[row][col] = -1 + Math.random()*2;
            }
        }
        return matrix;
    }

    public static void printMatrix(double[][] matrix){
        for(int r = 0; r < matrix.length; r++){
            for(int c = 0; c < matrix[r].length; c++){
                System.out.print(matrix[r][c]+ "\t");
            }
            System.out.println();
        }
    }

    public static double[][] NaiveAlgorithm(double[][] a, double[][] b){
        double[][] product = new double[a.length][b[0].length];
        double cell = 0;
        int addCounter = 0;
        int multCounter = 0;
        for(int row = 0; row < a.length; row++){
            for(int col = 0; col < a[0].length; col++){
                for(int i = 0; i < b.length; i++){
                    cell += a[row][i]*b[i][col];
                    if (row == 0 && col == 0)
			{
			    if (i == 0 || i == 1)
				{
				    continue;
				}
			}
		    else{
			addCounter++;
		    }
                    multCounter++;
                }
                product[row][col]= cell;
                cell = 0;
            }
        }
        System.out.println("The number of naive additions was " + addCounter);
        System.out.println("The number of naive multiplications was " + multCounter);
        return product;
    }

    //Matrix addition
    public static double[][] add(double[][] a, double[][] b) {
        counterAdd++;
        int n = a.length;
        double[][] sum = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sum[i][j] = a[i][j] + b[i][j];
            }
        }
        return sum;
    }
    //Matrix subtraction
    public static double[][] sub(double[][] a, double[][] b) {
        int n = a.length;
        double[][] diff = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                diff[i][j] = a[i][j] - b[i][j];
            }
        }
        return diff;
    }
    //Matrix multiplication
    public static double[][] mul(double[][] a, double[][] b) {
        counterMult++;
        int n = 2;
        double[][] prod = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                prod[i][j] = 0;
                for (int k = 0; k < n; k++) {
                    prod[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return prod;
    }

    private static double[][] strassen(double[][] a, double[][] b) {
        int n = a.length / 2;
        double[][] c = new double[2 * n][2 * n];

        double[][] a11 = new double[n][n];
        double[][] a12 = new double[n][n];
        double[][] a21 = new double[n][n];
        double[][] a22 = new double[n][n];
        double[][] b11 = new double[n][n];
        double[][] b12 = new double[n][n];
        double[][] b21 = new double[n][n];
        double[][] b22 = new double[n][n];

        if (a.length == 2) {
            c = mul(a, b); // base case
        } else {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    a11[i][j] = a[i][j];
                    a12[i][j] = a[i][j + n];
                    a21[i][j] = a[i + n][j];
                    a22[i][j] = a[i + n][j + n];
                    b11[i][j] = b[i][j];
                    b12[i][j] = b[i][j + n];
                    b21[i][j] = b[i + n][j];
                    b22[i][j] = b[i + n][j + n];
                }
            }

            double[][] p1 = sub(strassen(a11, b12), strassen(a11, b22));
            double[][] p2 = add(strassen(a11, b22), strassen(a12, b22));
            double[][] p3 = add(strassen(a21, b11), strassen(a22, b11));
            double[][] p4 = sub(strassen(a22, b21), strassen(a22, b11));
            double[][] p5 = strassen(add(a11, a22), add(b11, b22));
            double[][] p6 = strassen(sub(a12, a22), add(b21, b22));
            double[][] p7 = strassen(sub(a11, a21), add(b11, b12));

            double[][] c11 = sub(add(add(p5, p4), p6), p2);
            double[][] c12 = add(p1, p2);
            double[][] c21 = add(p3, p4);
            double[][] c22 = sub(sub(add(p1, p5), p3), p7);

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    c[i][j] = c11[i][j];
                    c[i][j + n] = c12[i][j];
                    c[i + n][j] = c21[i][j];
                    c[i + n][j + n] = c22[i][j];
                }
            }
        }
        return c;
    }

    public static double[][] strassenAlgorithm(double[][] a, double[][] b) {
        int k;
        int n = a.length;
        for (k = 0; n > 0; k++) {
            n >>= 1;
        }
        n = a.length;
        int nPower = (int) Math.pow(2, k);
        if (n == nPower / 2) { // n = 2^k
            return strassen(a, b);
        } else {
	    // Solves the problem when n != 2^k
            double[][] ap = new double[nPower][nPower];
            double[][] bp = new double[nPower][nPower];
            double[][] cp;
            double[][] c = new double[n][n]; // return value
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    ap[i][j] = a[i][j];
                    bp[i][j] = b[i][j];
                }
            }
	    // Add 0
            for (int i = 0; i < nPower; i++) {
                for (int j = 0; j < nPower; j++) {
                    ap[i][j] += 0;
                    bp[i][j] += 0;
                }
            }
            cp = strassen(ap, bp);
	    // Get rid of 0
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    c[i][j] = cp[i][j];
                }
            }
            return c;
        }
    }
}
