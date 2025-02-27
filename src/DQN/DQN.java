package DQN;

import Common.InputNeuron;
import Common.Neuron;
import Common.NeuronBase;

import java.io.*;
import java.util.*;

import static Common.Neuron.BATCH;

public class DQN {
    boolean train = false;
    static int fps = 120; // frames per second for cart pole animation
    public static String Filepath = "./src/DQN/"; // path to store weights of Neural Networks

    enum B0T_Type{ DQN, PROGRAMMED, NONE}
    enum Team{ RED, BLUE, NONE}

    public static B0T_Type B0T_Type = DQN.B0T_Type.DQN;
    public static Team B0T_Team = Team.RED;
    public static int MAX_POINTS = 10;

    static double EXPLORATION_SPACE = 50;
    static int M = 200;
    int maxEpisodeRewardRequired = 0;
    static int EPISODES = 30;
    static int EPOCH = 1;
    double discount = 0.99;
    int actionSpace = 3;

    double [][]Output_Batch ;
    double error;
    static Random randomGenerator = new Random();

    List<NeuronBase> q_InputLayer = new ArrayList<>();
    List<NeuronBase> q_L1_Layer = new ArrayList<>();
    List<NeuronBase> q_Output_Layer = new ArrayList<>();

    List<NeuronBase> target_InputLayer = new ArrayList<>();
    List<NeuronBase> target_L1_Layer = new ArrayList<>();
    List<NeuronBase> target_Output_Layer = new ArrayList<>();

    public static void main(String[] args) throws Exception{
        DQN dqn = new DQN();
        dqn.DeepQNetwork();
    }

    public void DeepQNetwork() throws IOException, InterruptedException {
        List<Double> avgRewardList = new ArrayList<>();
        double learn_rate = Math.pow(10,-4);
        long seed = -98;
        // Initialize Q network
        for (int j1 = 0; j1 < 2 * actionSpace; j1++) {
            q_L1_Layer.add(new Neuron()
                    .setLearning_Rate(learn_rate)
                    .setSeed(seed)
                    .setActivation(Neuron.Activation_Function.LINEAR)
                    .setWeightsFileName("Q_Network")
                    .setWeightsFileIndex(j1)
                    .setWeightInitializationAlgorithm(Neuron.Weight_Initialization.Xavier)
                    .setOptimizerAlgorithm(Neuron.Optimization_Algorithm.Adam)
                    .build());
        }

        for (int j1 = 0; j1 < actionSpace; j1++) {
            q_Output_Layer.add(new Neuron()
                    .setLearning_Rate(learn_rate)
                    .setSeed(seed)
                    .setActivation(Neuron.Activation_Function.LINEAR)
                    .setWeightsFileName("Q_Network")
                    .setWeightsFileIndex(j1 + q_L1_Layer.size())
                    .setWeightInitializationAlgorithm(Neuron.Weight_Initialization.Xavier)
                    .setOptimizerAlgorithm(Neuron.Optimization_Algorithm.Adam)
                    .build());
        }

        // Initialize target network
        for (int j1 = 0; j1 < 2 * actionSpace; j1++) {
            target_L1_Layer.add(new Neuron()
                    .setLearning_Rate(learn_rate)
                    .setSeed(seed)
                    .setActivation(Neuron.Activation_Function.LINEAR)
                    .setWeightsFileName("Target_Network")
                    .setWeightsFileIndex(j1)
                    .setWeightInitializationAlgorithm(Neuron.Weight_Initialization.Xavier)
                    .setOptimizerAlgorithm(Neuron.Optimization_Algorithm.Adam)
                    .build());
        }

        for (int j1 = 0; j1 < actionSpace; j1++) {
            target_Output_Layer.add(new Neuron()
                    .setLearning_Rate(learn_rate)
                    .setSeed(seed)
                    .setActivation(Neuron.Activation_Function.LINEAR)
                    .setWeightsFileName("Target_Network")
                    .setWeightsFileIndex(j1 + target_L1_Layer.size())
                    .setWeightInitializationAlgorithm(Neuron.Weight_Initialization.Xavier)
                    .setOptimizerAlgorithm(Neuron.Optimization_Algorithm.Adam)
                    .build());
        }

        for(int v=0 ; v < M && train; v++) {
            File trainingDataFile = new File(Filepath + "trainingData.csv");
            if (trainingDataFile.exists()) {
                System.out.println("trainingDataFile.delete() - " + v + " " + trainingDataFile.delete());
            }
            // createTrainingData
            double avgReward = 0;
            int game = 0;
            for (int episode = 0; episode < EPISODES; episode++) {
                Environment_Pong environment = new Environment_Pong();
                Environment_Pong.State curr_state = environment.getState();
                Environment_Pong.State new_state;

                while (true) {
                    List<Double> curr_state_NN_Input = currStateToInput(curr_state);
                    feedForward_Q_Network(curr_state_NN_Input, 0);

                    double[] q_Outputs = new double[actionSpace];
                    for (int j1 = 0; j1 < actionSpace; j1++) {
                        q_Outputs[j1] = q_Output_Layer.get(j1).getOutput();
                    }

                    int argMax = StateSpaceQuantization.argMax(q_Outputs);
                    if ((EXPLORATION_SPACE !=0) && ((nextDoubleBetween(0,1) < EXPLORATION_SPACE/M)))
                        argMax = nextIntBetween(-1,3);

                    int Q_at = sampleAction(argMax);

                    new_state = environment.getNewStateAndReward(Q_at);

                    double reward = new_state.reward;
                    avgReward = avgReward + reward;

                    List<Double> new_state_NN_Input = currStateToInput(new_state);

                    StoreTrainingData(curr_state_NN_Input, argMax, reward, new_state_NN_Input,new_state.d ? 1 : 0);

                    curr_state = new_state;
                    clearCache();

                    if( new_state.d) {
                        if( environment.p1_Score == 1 && environment.p2_Score == 0) {
                            System.out.println("TERMINOLO " + avgReward);
                            avgReward = 0;
                        }
                        break;
                    }
                    game++;
                }
            }
            System.out.println("AvgReward " + avgReward);
            avgRewardList.add(avgReward);
            if (avgReward >= maxEpisodeRewardRequired /*|| i==10000*/) {
                System.out.println("game number " + game+1);
                break;
            }
            List<List<String>> trainingSet = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("src/test/resources/trainingData.csv"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    trainingSet.add(Arrays.asList(values));
                }
            }


            BATCH = trainingSet.size();
            reInitBatchMemory();
            Output_Batch = new double[BATCH][];

            // Training Q Network with the help of Target Network
            int epoch = 0;
            while (train && (epoch++ < EPOCH)) {
                Collections.shuffle(trainingSet);
                for (int index = 0; index < trainingSet.size(); index++) {
                    try {
                        List<String> data = trainingSet.get(index);

                        int batchIndex = index % BATCH;

                        feedForward_Q_Network(stringToList(data.get(0)), batchIndex);
                        feedForward_Target_Network(stringToList(data.get(3)), batchIndex);

                        double[] q_Outputs = new double[actionSpace];
                        for (int j1 = 0; j1 < actionSpace; j1++) {
                            q_Outputs[j1] = q_Output_Layer.get(j1).getOutput();
                        }

                        double[] target_Outputs = new double[actionSpace];
                        for (int j1 = 0; j1 < actionSpace; j1++) {
                            target_Outputs[j1] = target_Output_Layer.get(j1).getOutput();
                        }

                        double Q_at = StateSpaceQuantization.max(q_Outputs);
                        double Q_atNext = StateSpaceQuantization.max(target_Outputs);
                        int j = StateSpaceQuantization.argMax(q_Outputs);

                        double reward = Double.parseDouble(data.get(2));

                        if (reward == -1)
                            error = reward - Q_at;
                        else
                            error = ((reward) + discount * Q_atNext) - Q_at;

                        Output_Batch[batchIndex] = formHotEncodedVector(error, j);

                        clearCache();

                        if ((index + 1) % BATCH == 0) {
                            for (int batchIndex1 = 0; batchIndex1 < Output_Batch.length; batchIndex1++) {
                                feedback_Q_Network(batchIndex1, Output_Batch[batchIndex1]);
                            }
                        }

                    } catch (Exception e) {
                        System.out.println("Exception:" + e);
                    }
                }
                copyWeights();
            }
            double avgEpisodeReward = 0;
            avgEpisodeReward = (findMean(avgRewardList, 4));
            if( v > 4) {
                System.out.println("avgEpisodeReward == " + avgEpisodeReward);
                if (avgEpisodeReward >= maxEpisodeRewardRequired) break;
            }
        }
        // Store Neural Network weights
        if( train) {
            File statsFile = new File(Filepath + "Q_Network.csv");
            if (statsFile.exists()) {
                System.out.println("statsFile.delete() - " + statsFile.delete());
            }
            File statsFile2 = new File(Filepath + "Target_Network.csv");
            if (statsFile2.exists()) {
                System.out.println("statsFile2.delete() - " + statsFile2.delete());
            }
            Store_ANN_Weights();
        }

        // Testing
        {
            Environment_Pong environment = new Environment_Pong();
            Environment_Pong.State curr_state = environment.getState();
            Environment_Pong.State new_state ;
            environment.pong.construct();
            environment.pong.init();
            Thread thread = new Thread(environment.pong);
            thread.start();

            while(true) {
                List<Double> curr_state_NN_Input = currStateToInput(curr_state);
                feedForward_Q_Network(curr_state_NN_Input, 0);

                double[] q_Outputs = new double[actionSpace];
                for (int j1 = 0; j1 < actionSpace; j1++) {
                    q_Outputs[j1] = q_Output_Layer.get(j1).getOutput();
                }
                int Q_at = sampleAction(StateSpaceQuantization.argMax(q_Outputs));

                new_state = environment.getNewStateAndReward(Q_at);

                curr_state = new_state;
                clearCache();
                if( new_state.d ){
                    break;
                }
                Thread.sleep(1000/fps);
            }
        }
        Thread.sleep(2000);
    }

    public void feedForward_Q_Network(List<Double> input, int batchIndex) throws IOException {
        for (Double d : input) {
            q_InputLayer.add(new InputNeuron(d));
        }

        if (!q_L1_Layer.isEmpty()){
            for (NeuronBase neuron : q_L1_Layer) {
                neuron.feedForward(q_InputLayer,batchIndex );
            }
        }

        if (!q_Output_Layer.isEmpty()) {
            for (NeuronBase neuron : q_Output_Layer) {
                neuron.feedForward(q_L1_Layer,batchIndex );
            }
        }
    }

    public void feedForward_Target_Network(List<Double> input, int batchIndex) throws IOException {
        for (Double integer : input) {
            target_InputLayer.add(new InputNeuron(integer));
        }

        if (!target_L1_Layer.isEmpty()){
            for (NeuronBase neuron : target_L1_Layer) {
                neuron.feedForward(target_InputLayer,batchIndex );
            }
        }

        if (!target_Output_Layer.isEmpty()) {
            for (NeuronBase neuron : target_Output_Layer) {
                neuron.feedForward(target_L1_Layer,batchIndex );
            }
        }
    }

    public void feedback_Q_Network(int batchIndex, double[] error){
        int q_action = getQ_Action(error);

        q_Output_Layer.get(q_action).setError_Next(error[q_action]);

        if (!q_Output_Layer.isEmpty()) {
            for (NeuronBase neuronBase : q_Output_Layer) {
                neuronBase.feedBack(batchIndex, 0);
            }
        }

        if (!q_L1_Layer.isEmpty()) {
            for (NeuronBase neuronBase : q_L1_Layer) {
                neuronBase.feedBack(batchIndex, 0);
            }
        }
    }

    public void clearCache(){
        q_InputLayer.clear();
        target_InputLayer.clear();
    }

    public int sampleAction(int argMax){
        if( argMax == 0)
            return -1;
        else if( argMax == 1)
            return +1;
        else return 0;
    }

    public static int getQ_Action(double[] Q) {
        for (int t = 0; t < Q.length; t++) {
            if (Q[t] != 0) {
                return t;
            }
        }
        return Q.length -1;
    }

    public void reInitBatchMemory(){
        if (!q_L1_Layer.isEmpty()){
            for (NeuronBase neuron : q_L1_Layer) {
                neuron.reInitializeBatchMemory();
            }
        }

        if (!q_Output_Layer.isEmpty()) {
            for (NeuronBase neuron : q_Output_Layer) {
                neuron.reInitializeBatchMemory();
            }
        }

        if (!target_L1_Layer.isEmpty()){
            for (NeuronBase neuron : target_L1_Layer) {
                neuron.reInitializeBatchMemory();
            }
        }

        if (!target_Output_Layer.isEmpty()) {
            for (NeuronBase neuron : target_Output_Layer) {
                neuron.reInitializeBatchMemory();
            }
        }
    }

    public List<Double> stringToList(String str){
        List<Double> integers = new ArrayList<>();
        String[] strings = str.split(";");
        for (String string : strings) {
            integers.add(Double.parseDouble(string));
        }
        return integers;
    }

    public double[] formHotEncodedVector(double yi, double j){
        double[] Y_Encoded = new double[actionSpace];
        for (int i=0; i< Y_Encoded.length; i++){
            if( i == j){
                Y_Encoded[i] = yi;
            }
            else Y_Encoded[i] = 0;
        }
        return Y_Encoded;
    }

    public void copyWeights(){
        for (int i=0 ; i< q_L1_Layer.size(); i ++){
            target_L1_Layer.get(i).setWeights(q_L1_Layer.get(i).getWeights());
        }

        for (int i=0 ; i< q_Output_Layer.size(); i ++){
            target_Output_Layer.get(i).setWeights(q_Output_Layer.get(i).getWeights());
        }
    }

    public void Store_ANN_Weights(){
        for (NeuronBase neuronL4 : q_L1_Layer) {
            neuronL4.Memorize();
        }

        for (NeuronBase neuronL4 : target_L1_Layer) {
            neuronL4.Memorize();
        }

        for (NeuronBase neuronL4 : q_Output_Layer) {
            neuronL4.Memorize();
        }

        for (NeuronBase neuronL4 : target_Output_Layer) {
            neuronL4.Memorize();
        }
    }

    public void StoreTrainingData(List<Double> s_t, double at, double reward, List<Double> s_tNext, double d){
        try {
            File statsFile = new File(Filepath + "trainingData.csv");
            if (statsFile.exists()) {
            } else {
                FileWriter out = new FileWriter(statsFile);
                out.flush();
                out.close();
            }

            if (statsFile.exists()) {
                FileWriter buf = new FileWriter(Filepath + "trainingData.csv", true);
                for (Double integer : s_t) {
                    buf.append(String.valueOf(integer));
                    buf.append(";");
                }
                buf.append(",");
                buf.append(String.valueOf(at));
                buf.append(",");
                buf.append(String.valueOf(reward));
                buf.append(",");
                for (Double integer : s_tNext) {
                    buf.append(String.valueOf(integer));
                    buf.append(";");
                }
                buf.append(",");
                buf.append(String.valueOf(d));
                buf.append("\n");
                buf.flush();
                buf.close();
            } else {
                System.out.println("StoreTrainingData FAIL 3 NO FILE");
            }
        }
        catch (Exception ex){
            System.out.println("StoreTrainingData FAIL 4"  + ex.getMessage());
        }
    }

    public double findMean (List<Double> X, int n) {
        double sum =0;
        double mean =0;
        // calculate mean and std of all columns
        for (int i = X.size()-1, k = 0; k < n && i>-1; i--, k++) {
            sum = (sum + X.get(i));
        }

        mean = sum/n;
        return mean;
    }

    public static double nextDoubleBetween(double min, double max) {
        return (Math.random() * (max - min)) + min;
    }

    public static int nextIntBetween(double min, double max) {
        return (int)((randomGenerator.nextDouble() * (max - min)) + min);
    }

    public List<Double> currStateToInput(Environment_Pong.State state){
        List<Double> input = new ArrayList<>();
        input.add((double)state.ball_x);
        input.add((double)state.ball_y);
        input.add((double)state.ball_xDirection);
        input.add((double)state.ball_yDirection);
        input.add((double)state.paddle_y);
        input.add((double)state.paddle_yDirection);
        return input;
    }
}
