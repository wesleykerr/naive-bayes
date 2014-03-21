package com.riotgames.bigdata.naive.bayes;

/**
 * This app will run the NaiveBayes classifier
 * on an input dataset and produce a model that 
 * can be used to compute a posterior probability
 * on a test set.
 *
 */
public class App {
    public static void main( String[] args ) {
        NaiveBayes test = new NaiveBayes();
        test.processLine("1 tag|namespace feature feature2=1.5 |namespace2 feature feature2=5");
    }
}
