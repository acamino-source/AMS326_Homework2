#include <iostream>
#include <cmath>
#include <random>

using namespace std;

const int INFECTED=10;
const int DAYS=45;
const double FIRST_MEAN=0.18;
const double FIRST_STD=0.04;
const double SECOND_MEAN=-0.24;
const double SECOND_STD=0.04;
const int NUMBER_POINTS=5;

void first45(double *values, double size)
{
    default_random_engine firstgenerator;
    firstgenerator.seed(time(0));
    normal_distribution<double> first(FIRST_MEAN,FIRST_STD);
    
    double percent_increase;
    for(int i=1;i<size;i++)
    {
        percent_increase=first(firstgenerator);
        values[i]=(percent_increase+1.0)*values[i-1];
    }
}

void second45(double *values, double size)
{
    default_random_engine secondgenerator;
    secondgenerator.seed(time(0));
    normal_distribution<double> second(SECOND_MEAN,SECOND_STD);
    
    double percent_decrease;
    for(int i=1;i<size;i++)
    {
        percent_decrease=second(secondgenerator);
        values[i]=(percent_decrease+1.0)*values[i-1];
    }
}

void output_values(double *values, double size, int starting_day)
{
    for (int i=0;i<size;i++)
    {
        cout<<"Day "<<i+starting_day<<"= "<<(int)values[i]<<endl;
    }
    
    return;
}

double **making_matrix(double *xpoints, double *ypoints)
{
    double **A=new double*[NUMBER_POINTS];
    for (int i=0;i<NUMBER_POINTS;i++)
    {
        A[i]= new double[NUMBER_POINTS+1];
    }
    
    for(int i=0;i<NUMBER_POINTS;i++)
    {
        int a=NUMBER_POINTS;
        for (int j=0;j<NUMBER_POINTS+1;j++)
        {
            if (j!=NUMBER_POINTS)
            {
                A[i][j]=pow(xpoints[i], a-1-j);
            }
            else
            {
                A[i][j]=ypoints[i];
            }
        }
    }
    return A;
}

double *gaussian(double **matrix)
{
    double *a=new double[NUMBER_POINTS];
    double b;
    
    for(int j=0;j<NUMBER_POINTS;j++)
    {
        for(int i=0;i<NUMBER_POINTS;i++)
        {
            if(i!=j)
            {
                b=matrix[i][j]/matrix[j][j];
                for(int k=0;k<NUMBER_POINTS+1;k++)
                {
                    matrix[i][k]=matrix[i][k]-b*matrix[j][k];
                }
            }
        }
    }
    
    for(int i=0;i<NUMBER_POINTS;i++)
    {
        a[i]=matrix[i][NUMBER_POINTS]/matrix[i][i];
        matrix[i][NUMBER_POINTS]=a[i];
        matrix[i][i]=1.0;
    }
    
    return a;
}

void output_matrix(double **matrix)
{
    for(int i=0;i<NUMBER_POINTS;i++)
    {
        for(int j=0;j<NUMBER_POINTS+1;j++)
        {
            cout<<"A["<<i<<"]["<<j<<"]= "<<matrix[i][j]<<endl;
        }
    }
}

double alpha_func(double *values, int size)
{
    double alpha;
    double xsum=0.0;
    double ysum=0.0;
    double xsquared_sum=0.0;
    double xy_sum=0.0;
    int count=0;
    
    for(int i=0;i<size;i++)
    {
        xsum=xsum+(i+DAYS);
        ysum=ysum+log(values[i]);
        xsquared_sum=xsquared_sum+pow(i+DAYS,2);
        xy_sum=xy_sum+((i+DAYS)*log(values[i]));
        count=count+1;
    }
    
    alpha=((ysum * xsquared_sum) - (xsum * xy_sum)) / ((count*xsquared_sum)-(pow(xsum,2)));
    
    return exp(alpha);
}

double beta_func(double *values, int size)
{
    double beta;
    double xsum=0.0;
    double ysum=0.0;
    double xsquared_sum=0.0;
    double xy_sum=0.0;
    int count=0;
    
    for(int i=0;i<size;i++)
    {
        xsum=xsum+(i+DAYS);
        ysum=ysum+log(values[i]);
        xsquared_sum=xsquared_sum+pow(i+DAYS,2);
        xy_sum=xy_sum+((i+DAYS)*log(values[i]));
        count=count+1;
    }
    
    beta=((count*xy_sum)-(xsum*ysum)) / ((count*xsquared_sum)-(pow(xsum,2)));
    
    return beta;
}

int main()
{
    double first[DAYS];
    double second[DAYS+1];
    double alpha;
    double beta;
    
    first[0]=INFECTED;
    first45(first,DAYS);
    cout<<"The daily number of infected people during the first 45 days is as followed: "<<endl;
    output_values(first,DAYS,1);
    
    cout<<endl;
    
    second[0]=first[DAYS-1];
    second45(second,DAYS+1);
    cout<<"The daily number of infected people during the second 45 days is as followed: "<<endl;
    output_values(second,DAYS+1,45);
    
    cout<<endl;
    
    double xpoints[NUMBER_POINTS]={9,18,27,36,45};
    double ypoints[NUMBER_POINTS]={first[8],first[17],first[26],first[35],first[44]};
    double **matrix=making_matrix(xpoints, ypoints);
    cout<<"The augmented matrix before gaussian elimination is as follows: "<<endl;
    output_matrix(matrix);
    
    cout<<endl;
    
    double *matrix_coef=gaussian(matrix);
    cout<<"The augmented matrix after gaussian elimination is as follows: "<<endl;
    output_matrix(matrix);
    
    cout<<endl;
    
    cout<<"Using the 5 specified days to interpolate the first 45 days, we get the equation: y(t)= "<<matrix_coef[0]<<"t^4 + ("<<matrix_coef[1]<<")t^3 + "<<matrix_coef[2]<<"t^2 + ("<<matrix_coef[3]<<")t + "<<matrix_coef[4]<<endl<<endl;
    
    alpha=alpha_func(second,DAYS);
    beta=beta_func(second,DAYS);
    cout<<"Fitting the results of the second 45 days to an exponential function gives us: y(t)=" <<alpha<<" * exp("<<beta<<"t)"<<endl;
    
    return 0;
}

