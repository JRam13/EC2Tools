package ec2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AttachVolumeRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.DetachVolumeRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Volume;

public class EC2Tools {
	
	static int option;
	static AmazonEC2 ec2;
	private static Volume currVol;
	private static boolean instanceSelected = false;
	static String currInstance;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		ec2 = new AmazonEC2Client(new ClasspathPropertiesFileCredentialsProvider());
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		ec2.setRegion(usWest2);
		
		System.out.println("===========================================");
		System.out.println("EC2 Tools");
	    System.out.println("===========================================\n");
	     
		while (option != 7) {
       
	        System.out.println("What would you like to do: ");
	        System.out.println("--------------------------");
	        
	        System.out.println("1) Setup Secret/Access Keys (One-time setup)");
	        System.out.println("2) List / Select EC2 Instances");
	        System.out.println("3) List Attached Volumes");
	        System.out.println("4) List Unattached Volumes");
	        System.out.println("5) Detach A Volume");
	        System.out.println("6) Attach A Volume");
	        System.out.println("7) Exit");
	        
	        Scanner scn = new Scanner(System.in); 
			System.out.println("Choose a number: ");
			try {
				option = scn.nextInt();
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
		
			if(option == 1){
				System.out.println();
				setKeys();
			}
			else if(option == 2){
				System.out.println();
				listInstances();
			}
			else if(option == 3){
				System.out.println();
				attachedVolumes();
			}
			else if(option == 4){
				System.out.println();
				unattachedVolumes();
			}
			else if(option == 7){
				System.out.println("Goodbye.");
				System.exit(1);
			}
			else if(option == 5){
				System.out.println();
				detachVolume();
			}
			else if(option == 6){
				System.out.println();
				attachVolume();
			}
			else{
				System.err.println("Please select a valid option");
			}
		}
    	

    }
	
    private static void setKeys() throws IOException{
    	
    	//Ask users for AWS keys
    	
    	System.out.println("Create AWS Keys");
        System.out.println("--------------------------\n");
    	
    	
    	// Location of file to read
        File file = new File("src/AwsCredentials.properties");
        String secretK = "";
        String accessK = "";
        	
        System.out.println("AWS Access Keys One-Time Setup");
    	
    	Scanner scn = new Scanner(System.in); 
		System.out.println("Insert Secret Key: ");
		secretK = scn.nextLine();
		System.out.println("Insert Access Key: ");
		accessK = scn.nextLine();
		System.out.println("AWS Keys Vertified");
		
		FileWriter fr = new FileWriter(file);
		BufferedWriter br = new BufferedWriter(fr);
		
		java.util.Date date= new java.util.Date();
		 
		 
		br.write("#Insert your AWS Credentials from http://aws.amazon.com/security-credentials");
		br.newLine();
		br.write("#" + new Timestamp(date.getTime()));
		br.newLine();
		br.write("secretKey=" + secretK);
		br.newLine();
		br.write("accessKey=" + accessK);
		br.close();
		System.out.println();
 
    }
    
    private static void listInstances(){
    	System.out.println("List Instances");
        System.out.println("--------------------------\n");
        System.out.println("Describe Current Instances");
        DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
        List<Reservation> reservations = describeInstancesRequest.getReservations();
        Set<Instance> instances = new HashSet<Instance>();
        // add all instances to a Set.
        int count = 1;
        for (Reservation reservation : reservations) {
         instances.addAll(reservation.getInstances());
        }
        
        System.out.println("You have " + instances.size() + " Amazon EC2 instance(s).");
        for (Instance ins : instances){
        
         // instance id
         String instanceId = ins.getInstanceId();
        
         // instance state
         InstanceState is = ins.getState();
         System.out.println(count + ") " + instanceId+" "+is.getName());
         count++;
        }
        
        Scanner scn = new Scanner(System.in); 
     	int optionB = 0;
		try {
 			optionB = scn.nextInt();
 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			System.err.println("Please select a proper option");
 			listInstances();
 		}
 		
 		if(optionB > count - 1 || optionB < 1){
 			System.out.println("Please select a proper bucket");
 			listInstances();
 		}
 		count = 1;
        for (Instance ins : instances){
            
            // instance id
            String instanceId = ins.getInstanceId();
            if(optionB == count){
            	currInstance = instanceId;
            	instanceSelected = true;
            }
            count++;
           }
 		
        System.out.println();
    }

    private static void attachedVolumes(){
    	System.out.println("List Attached Volumes");
        System.out.println("--------------------------\n");
        System.out.println("Describe Current Volumes");
        DescribeVolumesResult describeVolume = ec2.describeVolumes();
        List<Volume> volumes = describeVolume.getVolumes();
        
        
        int count = 1;
        System.out.println("You have " + volumes.size() + " Total Amazon EC2 volume(s).");
        for (Volume vol : volumes){
        
         // volume id
         String volId = vol.getVolumeId();
        
         // volume state
         String volState = vol.getState();
         String volType = vol.getVolumeType();
         if(volState.equals("in-use")){
        	 System.out.println(count + ") " + volId+" "+ volState + " " + volType);
             count++;
         }
         
        }
        System.out.println();
    }
    
    private static void unattachedVolumes(){
    	System.out.println("List Unattached Volumes");
        System.out.println("--------------------------\n");
        System.out.println("Describe Current Volumes");
        DescribeVolumesResult describeVolume = ec2.describeVolumes();
        List<Volume> volumes = describeVolume.getVolumes();
        
        
        int count = 1;
        System.out.println("You have " + volumes.size() + " Total Amazon EC2 volume(s).");
        for (Volume vol : volumes){
        
         // volume id
         String volId = vol.getVolumeId();
        
         // volume state
         String volState = vol.getState();
         String volType = vol.getVolumeType();
         if(volState.equals("in-use")){
        	 
         }else{
        	 System.out.println(count + ") " + volId+" "+ volState + " " + volType);
             count++;
         }
         
        }
        System.out.println();
    }
    
    private static void detachVolume(){
    	System.out.println("List Attached Volumes");
        System.out.println("--------------------------\n");
        System.out.println("Select A Volume To Detach");
        DescribeVolumesResult describeVolume = ec2.describeVolumes();
        List<Volume> volumes = describeVolume.getVolumes();
        
        
        int count = 1;
        System.out.println("You have " + volumes.size() + " Total Amazon EC2 volume(s).");
        for (Volume vol : volumes){
        
         // volume id
         String volId = vol.getVolumeId();
        
         // volume state
         String volState = vol.getState();
         String volType = vol.getVolumeType();
         if(volState.equals("in-use")){
        	 System.out.println(count + ") " + volId+" "+ volState + " " + volType);
             count++;
         }
         
       }
        
        Scanner scn = new Scanner(System.in); 
     	int optionB = 0;
		try {
 			optionB = scn.nextInt();
 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			System.err.println("Please select a proper option");
 			detachVolume();
 		}
 		
 		if(optionB > count - 1 || optionB < 1){
 			System.out.println("Please select a proper bucket");
 			detachVolume();
 		}
 		count = 1;
        for (Volume vol : volumes){
           
            // volume state
            String volState = vol.getState();
            if(volState.equals("in-use")){
           	 if(optionB == count){
           		 currVol = vol;
           		 
           		 ec2.detachVolume(new DetachVolumeRequest(currVol.getVolumeId()));
           		System.err.println("VOLUME " + currVol.getVolumeId() + " IS NOW DETACHED!");
           		break;
           	 }
                count++;
            }
            
          }
 		
        System.out.println();
    }
    
    private static void attachVolume(){
    	
    	if (instanceSelected){
	    	System.out.println("List Unattached Volumes");
	        System.out.println("--------------------------\n");
	        System.out.println("Describe Current Volumes");
	        DescribeVolumesResult describeVolume = ec2.describeVolumes();
	        List<Volume> volumes = describeVolume.getVolumes();
	        
	        
	        int count = 1;
	        System.out.println("You have " + volumes.size() + " Total Amazon EC2 volume(s).");
	        for (Volume vol : volumes){
	        
	         // volume id
	         String volId = vol.getVolumeId();
	        
	         // volume state
	         String volState = vol.getState();
	         String volType = vol.getVolumeType();
	         if(volState.equals("in-use")){
	        	 
	         }else{
	        	 System.out.println(count + ") " + volId+" "+ volState + " " + volType);
	             count++;
	         }
	         
	         
	        }
	        Scanner scn = new Scanner(System.in); 
	     	int optionB = 0;
			try {
	 			optionB = scn.nextInt();
	 		} catch (Exception e) {
	 			// TODO Auto-generated catch block
	 			System.err.println("Please select a proper option");
	 			attachVolume();
	 		}
	 		
	 		if(optionB > count - 1 || optionB < 1){
	 			System.out.println("Please select a proper bucket");
	 			attachVolume();
	 		}
	 		
	 		count = 1;
	        for (Volume vol : volumes){
	           
	            // volume state
	            String volState = vol.getState();
	            if(volState.equals("in-use")){
	           	 
	            }else{
	            	if(optionB == count){
	              		 currVol = vol;
	            	}
	                count++;
	            }
	            
	            
	           }
	        
	        AttachVolumeRequest attachVol = new AttachVolumeRequest();
	        attachVol.withInstanceId(currInstance);
	        attachVol.withVolumeId(currVol.getVolumeId());
	        Random generator = new Random();
	        int r = generator.nextInt(99);
	        attachVol.withDevice("/dev/sda"+r); 
	        ec2.attachVolume(attachVol);
	        System.err.println(currVol.getVolumeId() + " Is Now Attached To Instance: "+ currInstance);
	 		
	 		
	        
	    }
    
    else{
    	System.err.println("You must select an Instance first");
    }
    	System.out.println();
    }
}
