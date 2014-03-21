/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.riotgames.bigdata.naive.bayes;

import com.riotgames.bigdata.hash.MurmurHash3;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This is an implementation of naive-bayes that
 * performs a single pass over the data.  It assumes Gaussian 
 * distributions for each of the features and estimates the 
 * parameters of that Gaussian.
 * 
 * The feature set needs to be known ahead of time to properly
 * estimate the parameters for the distributions.
 *
 * @author wkerr
 */
public class NaiveBayes {
    
    /** Fixed seed for now */
    private static final int SEED = 101;
    
    /* a value *much* larger than the expected number of features that we
     * want to be able handle.
     */
    private static final int MAGIC = (int) Math.pow(2,20);
    
    public static final int MEAN = 0;
    public  static final int M2 = 1;
    public static final int N = 2;

    /** the feature model will store the intermediate values for online variance */
    private double[][][] _featureModel = new double[2][MAGIC][3];
    private Set<String> _dictionary;
    
    private boolean _learn = true;
    
    
    public NaiveBayes() { 
        _dictionary = new LinkedHashSet<String>();
    }
    
    /**
     * Turn on/off learning.
     * @param learn
     */
    public void setLearning(boolean learn) { 
        _learn = learn;
    }
    
    /**
     * Update the model for the given feature and value.
     * @param label
     * @param featureName
     * @param featureValue 
     */
    public void update(int label, String featureName, double featureValue) { 
        int hashValue = MurmurHash3.murmurhash3_x86_32(featureName, 0, featureName.length(), SEED);
        int idx = (hashValue & 0x7FFFFFFF) % MAGIC;

        System.out.println("Update: " + featureName + "[" + hashValue + "," + idx + "] = " + featureValue);
        
        double[] model = _featureModel[label][idx];
        model[N] += 1;
        
        double delta = featureValue - model[MEAN];
        model[MEAN] += (delta / model[N]);
        model[M2] += (delta * (featureValue - model[MEAN]));
    }
        
    /**
     * This is used for testing purposes only.
     * @param label
     * @param namespace
     * @param feature
     * @return 
     */
    public double[] getParams(int label, String namespace, String feature) { 
        String featureName = namespace+"::"+feature;
        int hashValue = MurmurHash3.murmurhash3_x86_32(featureName, 0, featureName.length(), SEED);
        int idx = (hashValue & 0x7FFFFFFF) % MAGIC;
        
        return _featureModel[label][idx];
    }
    
    /**
     * Parse the input line and update the model.
     * @param line 
     */
    public double processLine(String line) { 
        
        // first pull off the label.
        int index = line.indexOf('|');
        if (index == -1) 
            throw new RuntimeException("Invalid input: missing features");
        
        String frontMatter = line.substring(0, index);
        String[] tokens = frontMatter.split(" ");
        int label = Integer.parseInt(tokens[0]);

        String tag = tokens[1];
        System.out.println("Label: " + label + " tag: " + tag);
        
        String features = line.substring(index);
        
        double prediction = 0.0;
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
                
                String featureName;
                double featureValue = 1.0;
                
                StringBuilder buf = new StringBuilder();
                for (int j = i; j < features.length() && features.charAt(j) != ' '; ++j) {
                    if (features.charAt(j) != '=')
                        buf.append(features.charAt(j));
                    else { 
                        // we are now processing the real value associated
                        // with this feature.
                        StringBuilder bufFV = new StringBuilder();
                        for (int k = j+1; k < features.length() && features.charAt(k) != ' '; ++k)  
                            bufFV.append(features.charAt(k));
                        
                        featureValue = Double.parseDouble(bufFV.toString());
                        j += bufFV.length();
                        i += bufFV.length()+1;
                    }
                }
                
                featureName = buf.toString();
                System.out.println("Feature -- " + featureName + "-" + featureValue);

                String fullName = namespace+"::"+featureName;
                update(label, fullName, featureValue);
                
                // updated, now we need to compute
                int hashValue = MurmurHash3.murmurhash3_x86_32(featureName, 0, featureName.length(), SEED);
                int idx = (hashValue & 0x7FFFFFFF) % MAGIC;
                
                double[] params = _featureModel[label][idx];
                
                double variance = params[M2] / (params[N]-1);
                double prefix = 1 / (2.0 * Math.PI * variance);
                double exponent = - Math.pow(featureValue - params[MEAN],2) / (2*variance);
                prediction += Math.log(prefix * Math.exp(exponent));

                i += buf.length();
            }
        }
        
        return prediction;
    }
    
    private class ClassDetails { 
        private int _id;
        private int _n;
        
        private Map<Integer,double[]> _models;
        
        public ClassDetails(int id) {
            _id = id;
            _n = 0;
            
            _models = new HashMap<Integer,double[]>();
        }
    }
}
