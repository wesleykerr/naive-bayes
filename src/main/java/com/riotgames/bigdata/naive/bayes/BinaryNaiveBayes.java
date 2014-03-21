/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.riotgames.bigdata.naive.bayes;

import com.riotgames.bigdata.hash.MurmurHash3;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is an implementation of naive-bayes that
 * performs a single pass over the data.  It assumes binary
 * features that are either present or missing.  To estimate
 * the likelihood we count the number of occurrences.

 * @author wkerr
 */
public class BinaryNaiveBayes {

    private static final int NUM_CLASSES = 2;
    
    /** Fixed seed for now */
    private static final int SEED = 101;
    
    /* a value *much* larger than the expected number of features that we
     * want to be able handle.
     */
    private static final int MAGIC = (int) Math.pow(2,20);

    /** the feature model will store the intermediate values for online variance */
    private double[][] _countsByFeature = new double[NUM_CLASSES][MAGIC];
    private double[]   _countsByClass = new double[NUM_CLASSES];
    private double     _counts        = 0;

    /** this stores the word counts for a unigram word model, so each
      * training instance is a collection of words -- this stores the 
      * total number of words 
      */
    private double[]   _featuresByClass = new double[NUM_CLASSES];

    /** we need to keep around the list of features for Add-One smoothing */
    private Set<Integer> _features = new HashSet<Integer>();
    

    /**
     * Parse the input line and update the model.
     * @param line 
     */
    public double processLine(String line, boolean learn) { 
        
        // first pull off the label.
        int index = line.indexOf('|');
        if (index == -1) 
            throw new RuntimeException("Invalid input: missing features");
        
        String frontMatter = line.substring(0, index);
        String[] tokens = frontMatter.split(" ");

        int label = Integer.parseInt(tokens[0]);
        if (learn) {
            ++_counts;
            ++_countsByClass[label];
        }

        String tag = tokens[1];
        System.out.println("Label: " + label + " tag: " + tag);
        
        String features = line.substring(index);
        List<Integer> instanceFeatures = new ArrayList<Integer>();
        List<String> instanceFeatureNames = new ArrayList<String>();
        
        String namespace="";
        for (int i = 0; i < features.length(); ++i) { 
            if (features.charAt(i) == '|') {
                // advance to the next space and replace the
                StringBuilder buf = new StringBuilder();
                for (int j = i+1; j < features.length() && features.charAt(j) != ' '; ++j) 
                    buf.append(features.charAt(j));
                
                namespace = buf.toString();
                System.out.println("Namespace change -- " + namespace);
                i += buf.length() + 1;
            } else { 
                // we are processing a feature....
                StringBuilder buf = new StringBuilder();
                for (int j = i; j < features.length() && features.charAt(j) != ' '; ++j) {
                    if (features.charAt(j) != '=')
                        buf.append(features.charAt(j));
                    else { 
                        throw new RuntimeException("Only binary values allowed!");
                    }
                }
                
                String featureName = namespace + "::" + buf.toString();
                System.out.println("Feature -- " + featureName);
            
                int hashValue = MurmurHash3.murmurhash3_x86_32(featureName, 0, featureName.length(), SEED);
                int idx = (hashValue & 0x7FFFFFFF) % MAGIC;

                if (learn) { 
                    _countsByFeature[label][idx] += 1;
                    _featuresByClass[label] += 1;
                }
                i += buf.length();
                
                _features.add(idx);
                instanceFeatures.add(idx);
                instanceFeatureNames.add(featureName);
            }
        }
        
        
        // compute the posterior value for each class so that we can
        // properly normalize everything...
        
        double[] unnormalized = new double[NUM_CLASSES];
        double sum = 0.0;
        for (int i = 0; i < NUM_CLASSES; ++i) { 
            
            double p = 1.0; // _countsByClass[i] / _counts;
            double logp = Math.log(p);
            for (int j = 0; j < instanceFeatures.size(); ++j) { 
                String name = instanceFeatureNames.get(j);
                int idx = instanceFeatures.get(j);

                double top = _countsByFeature[i][idx] + 1;
                double bottom = _featuresByClass[i] + _features.size();
                
                System.out.println("\t\tP(" + name + " | " + i + ") = " + top + " / " + bottom + " = " + (top / bottom));
                
                p *= (top / bottom);
                logp += Math.log(top / bottom);
            }
            System.out.println(i + " -- " + p);
            
            double prior = _countsByClass[i] / _counts;
            System.out.println("\tprior(" + i + ") = " + prior);

            p *= prior;
            System.out.println("\tP(" + i + " | data) = " + p + " / P(data)");
            
            unnormalized[i] = p;
            sum += p;
        } 
        
        for (int i = 0; i < NUM_CLASSES; ++i) {
            unnormalized[i] /= sum;
            System.out.println("P(" + i + " | data) = " +  unnormalized[i]);
        }
        return unnormalized[label];
    }  
}
