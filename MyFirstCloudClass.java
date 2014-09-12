package com.cmpe281.testPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateImageRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.opsworks.AWSOpsWorksClient;
import com.amazonaws.services.opsworks.model.DeleteInstanceRequest;
import com.amazonaws.services.opsworks.model.StartInstanceRequest;
import com.amazonaws.services.simpleworkflow.flow.core.TryCatchFinally.State;

public class MyFirstCloudClass {

	public static final String INSTANCE_STATE_PENDING 		= "pending";
	public static final String INSTANCE_STATE_RUNNING 		= "running";
	public static final String INSTANCE_STATE_SHUTTINGDOWN 	= "shutting-down";
	public static final String INSTANCE_STATE_TERMINATED 	= "terminated";
	public static final String INSTANCE_STATE_STOPPING 		= "stopping";
	public static final String INSTANCE_STATE_STOPPED 		= "stopped";
	public static final String INSTANCE_STATE_STARTING 		= "starting";
	public static final String INSTANCE_STATE_STARTED 		= "started";
	//pending | running | shutting-down | terminated | stopping | stopped
	public static void main(String[] args) {


		//AWS variable declaration - Start
		AWSCredentials credentials;
		//AWS variable declaration - End

		//Application variable - Start
		Scanner scr 					= new Scanner(System.in); //To get User input
		boolean bEndApplication 		= false;		
		int iCount						= 0;
		String strInput 				= null; 
		String strAmiId					= "ami-a02015e5"; // Microsoft Windows Server 2012 Base;
		String ec2InstanceId;
		List<String> lsInstancesId 		= new LinkedList<String>();
		ArrayList<String> arr  			= new ArrayList<String>();
		//Application variable - End

		System.out.println("=======================================\nAWS Management console::::Cmpe 281 LAB1\n=======================================");		

		try {
			// To read property file AwsCredentials.properties to get the access key
			credentials = new PropertiesCredentials( AwsConsoleApp.class.getResourceAsStream("AwsCredentials.properties"));
			// Main component - Amazon EC2 client
			AmazonEC2Client myAmazonEC2Client = new AmazonEC2Client(credentials);

			// Set Endpoint - Also used to fetch reservations
			myAmazonEC2Client.setEndpoint("ec2.us-west-1.amazonaws.com");//Site 2 US West (Northern California) Region
			//myAmazonEC2Client.setEndpoint("ec2.us-west-2.amazonaws.com");//Site 1 US West (Oregon) Region
			//myAmazonEC2Client.setEndpoint("ec2.us-east-1.amazonaws.com");//Site 3 US East (Northern Virginia) 
			//myAmazonEC2Client.setEndpoint("ec2.eu-west-1.amazonaws.com");//Site 4 EU (Ireland) 
			//myAmazonEC2Client.setEndpoint("ec2.ap-southeast-1.amazonaws.com");//Site 5 Asia Pacific (Singapore)
			//myAmazonEC2Client.setEndpoint("ec2.ap-southeast-2.amazonaws.com");//Site 6 Asia Pacific (Sydney)
			//myAmazonEC2Client.setEndpoint("ec2.ap-northeast-1.amazonaws.com");//Site 7 Asia Pacific (Tokyo)
			//myAmazonEC2Client.setEndpoint("ec2.sa-east-1.amazonaws.com");//Site 6 South America (Sao Paulo)

			while(!bEndApplication){
				System.out.println("Please enter the choice to perform corresponding operation ?"
						+ "\n1. Create an Instance"
						+ "\n2. Start an Instance"
						+ "\n3. Stop an Instance"
						+ "\n4. Terminate an Instance"
						+ "\n5. Reboot an Instance"
						+ "\n6. List Instance details"
						+ "\n7. Create an AMI"
						+ "\n8. Exit");
				strInput = scr.next();
				switch(strInput){

				case "1"://Create an instance 

					// String keyName = "UserDefinedKeyPair";// Can get it as an input from the user
					/*
					 * Create a user defined key pair
					 *
					 * 
				CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();					    	
				createKeyPairRequest.withKeyName(keyName);
				CreateKeyPairResult createKeyPairResult = myAmazonEC2Client.createKeyPair(createKeyPairRequest);
				KeyPair keyPair = new KeyPair();
				keyPair = createKeyPairResult.getKeyPair();
				String privateKey = keyPair.getKeyMaterial();
				System.out.println("Key created successfully");
					 */

					// String strSecurityGrp = "UserDefinedSecurityGrp";// Can get it as an input from the user
					/*
					 * Create a security group
					 */

					/*
				CreateSecurityGroupRequest createSecurityGroupRequest =	new CreateSecurityGroupRequest();//Security group
				createSecurityGroupRequest.withGroupName("JavaSecurityGroup2").withDescription("My Java Security Group2");
				String strSecurityGrp = createSecurityGroupRequest.getGroupName();
				System.out.println("strSecurityGrp>>>>>>>>>>>>>>"+strSecurityGrp);
				CreateSecurityGroupResult createSecurityGroupResult = myAmazonEC2Client.createSecurityGroup(createSecurityGroupRequest);
				System.out.println("SG created succesfully");
					 */
					System.out.println("\nCreating Instance\n=========================\n");
					RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
					runInstancesRequest.withImageId(strAmiId)
					.withInstanceType("t1.micro")
					.withMinCount(1)
					.withMaxCount(1);
					//				.withKeyName(keyName)
					//				.withSecurityGroups(strSecurityGrp);
					RunInstancesResult runInstancesResult = myAmazonEC2Client.runInstances(runInstancesRequest);
					Set<Instance> instanceSet = new HashSet<Instance>();
					String instanceId = null;
					Reservation revCreatedRevs = runInstancesResult.getReservation();
					instanceSet.addAll(revCreatedRevs.getInstances());

					for (Instance ins : instanceSet){
						// Fetch the instance id
						instanceId = ins.getInstanceId();
						System.out.println("Creating an instance with instance ID:::"+instanceId+"\n");
					}

					break;
				case "2":
					/*
					 * Start an instance
					 */
					arr = new ArrayList<String>();
					//System.out.println("1");
					arr = instanceDetails(myAmazonEC2Client, false, false);
					if(arr.size() != 0){
						System.out.println("\n Start an Instance\n=========================\n");
						iCount = fetchChoice();
						if(!(iCount > arr.size()) && iCount>0){
							ec2InstanceId = (String) arr.get(iCount-1);
							lsInstancesId = new LinkedList<String>();
							lsInstancesId.add(ec2InstanceId);
							StartInstancesRequest objStartInstancesRequest = new StartInstancesRequest();
							objStartInstancesRequest.setInstanceIds(lsInstancesId);
							myAmazonEC2Client.startInstances(objStartInstancesRequest);
							System.out.println("Starting instance with id: "+lsInstancesId+"\n\n");
						}
						else{
							System.out.println("\nInvalid input!!!\n");
						}

					}
					break;
				case "3"://Stop
					/*
					 * Stop an instance
					 */
					arr = new ArrayList<String>();
					//System.out.println("2");
					arr = instanceDetails(myAmazonEC2Client, false, false);
					if(arr.size() != 0){
						System.out.println("\n Stop an Instance\n=========================\n");
						iCount = fetchChoice();
						if(!(iCount > arr.size()) && iCount>0){
							ec2InstanceId = (String) arr.get(iCount-1);
							lsInstancesId = new LinkedList<String>();
							lsInstancesId.add(ec2InstanceId);
							StopInstancesRequest objStopInstancesRequest = new StopInstancesRequest();
							objStopInstancesRequest.setInstanceIds(lsInstancesId);
							myAmazonEC2Client.stopInstances(objStopInstancesRequest);
							System.out.println("Stopping instance with id: "+lsInstancesId+"\n\n");
						}
						else{
							System.out.println("\nInvalid input!!!\n");
						}

					}
					break;
				case "4"://Terminate
					/*
					 * Terminate an instance
					 */
					arr = new ArrayList<String>();
					//System.out.println("3");
					arr = instanceDetails(myAmazonEC2Client, false, true);
					if(arr.size() != 0){
						System.out.println("\n Terminate an Instance\n=========================\n");
						iCount = fetchChoice();
						if(!(iCount > arr.size()) && iCount>0){
							ec2InstanceId = (String) arr.get(iCount-1);
							TerminateInstancesRequest objTerminateInstancesRequest = new TerminateInstancesRequest();
							lsInstancesId = new LinkedList<String>();
							lsInstancesId.add(ec2InstanceId);
							objTerminateInstancesRequest.setInstanceIds(lsInstancesId);
							myAmazonEC2Client.terminateInstances(objTerminateInstancesRequest);
							System.out.println("\n Terminated instance with id: "+lsInstancesId+"\n\n");

						}
						else{
							System.out.println("\nInvalid input!!!\n");
						}

					}
					break;
				case "5"://Reboot
					/*
					 * Reboot an instance
					 */
					arr = new ArrayList<String>();
					//System.out.println("4");
					arr = instanceDetails(myAmazonEC2Client, true, false);
					if(arr.size() != 0){
						System.out.println("\n Reboot process\n=========================\n");
						iCount = fetchChoice();
						if(!(iCount > arr.size()) && iCount>0){
							ec2InstanceId = (String) arr.get(iCount-1);
							lsInstancesId = new LinkedList<String>();
							lsInstancesId.add(ec2InstanceId);
							RebootInstancesRequest objRebootInstancesRequest = new RebootInstancesRequest();
							objRebootInstancesRequest.setInstanceIds(lsInstancesId);
							myAmazonEC2Client.rebootInstances(objRebootInstancesRequest);
							System.out.println("Rebooting instance with id: "+lsInstancesId);
						}
						else{
							System.out.println("\nInvalid input!!!\n");
						}
					} 
					break;
				case "6"://List Instance details
					/*
					 * Get instance information such as Id, State...
					 */
					//System.out.println("5");
					instanceDetails(myAmazonEC2Client, false, false);
					break;
				case "7":
					/*
					 *Creating an image  
					 */
					arr = new ArrayList<String>();
					//System.out.println("6");
					arr = instanceDetails(myAmazonEC2Client, false, false);
					if(arr.size() != 0){
						System.out.println("\n Create an AMI\n=========================\n");
						iCount = fetchChoice();
						if(!(iCount > arr.size()) && iCount>0){
							ec2InstanceId = (String) arr.get(iCount-1);
							CreateImageRequest objCreateImageRequest = new CreateImageRequest();
							objCreateImageRequest.setInstanceId(ec2InstanceId);
							objCreateImageRequest.setName("MyCustomAMI");
							myAmazonEC2Client.createImage(objCreateImageRequest);
							System.out.println("Creating AMI with instance id: "+ec2InstanceId+"\n\n");
						}
						else{
							System.out.println("\nInvalid input!!!\n");
						}
					}
					break;
				case "8":
					/*
					 * Exit the application
					 */
					System.out.println("\nThanks for using Madan's AWS application\n");
					bEndApplication = true;
					break;
				default: 
					System.out.println("\nInvalid Input !!!\n\nApplication Terminated");
					bEndApplication = true;
					break;
				}
			}

		} catch (IOException e) {
			System.out.println("Error in cloud project");
			e.getMessage();
			e.printStackTrace();
		}
	}

	/*
	 * List and describe characteristics of existing instance
	 * Printing Instance count and instance id and corresponding states
	 * input Amazon client class object
	 * Output array of instance IDs
	 */

	@SuppressWarnings("resource")
	private static int fetchChoice() {
		System.out.println("\nPlease enter the choice(numeric only) of instance to Perform the operation :: \n");
		int iCount = 0;
		try{
			Scanner scrChoice = new Scanner(System.in);
			iCount = scrChoice.nextInt();
		}
		catch(Exception e){
			//System.out.println("Invalid Input");
		}
		return iCount;

	}

	public static ArrayList<String> instanceDetails(AmazonEC2Client myAmazonEC2Client, boolean bReboot, boolean bTerminate){

		InstanceState objIState  = null;
		String instanceState	 = null;
		String instanceId;
		int iCount = 0;
		ArrayList<String> arrIns = new ArrayList<>();
		DescribeInstancesResult describeInstancesRequest = null;
		Set<Instance> instanceSet 		= new HashSet<Instance>();
		List<Reservation> reservations 	= null;

		describeInstancesRequest = myAmazonEC2Client.describeInstances();//Used to get the details of the AWS instances - Contains reservations
		reservations = describeInstancesRequest.getReservations();//Fetching reservations


		for (Reservation reservation : reservations) {// Loop to add all the instance from the reservation to a set
			instanceSet.addAll(reservation.getInstances());
		}
		System.out.println("\nYou have " + instanceSet.size() + " Amazon EC2 instance(s).\n");

		for (Instance ins : instanceSet){
			// Fetch the instance id
			instanceId = ins.getInstanceId();
			// Fetch the instance state
			objIState  = ins.getState();
			instanceState = objIState.getName();
			if(instanceState != null && !"".equalsIgnoreCase(instanceState) 
					&& !INSTANCE_STATE_PENDING.equalsIgnoreCase(instanceState)
					&& !INSTANCE_STATE_SHUTTINGDOWN.equalsIgnoreCase(instanceState)
					&& !INSTANCE_STATE_STOPPING.equalsIgnoreCase(instanceState)
					&& !INSTANCE_STATE_STARTING.equalsIgnoreCase(instanceState)){
				//System.out.println(bReboot);
				if(!(bReboot && (INSTANCE_STATE_STOPPED.equalsIgnoreCase(instanceState) 
						|| INSTANCE_STATE_TERMINATED.equalsIgnoreCase(instanceState)))){
					if(!(bTerminate && (INSTANCE_STATE_RUNNING.equalsIgnoreCase(instanceState)))){
						arrIns.add(instanceId);
						iCount++;
						System.out.println(iCount+". "+instanceId+" "+instanceState+"\n");
					}
					else{
						System.out.println(instanceId+" "+instanceState+"*\n");
					}
				}
				else{
					System.out.println(instanceId+" "+instanceState+"*\n");
				}
			}
			else{
				System.out.println(instanceId+" "+instanceState+"*\n");
			}
		}


		if(arrIns.size() <= 0){
			System.out.println("\nNo valid instances found\n");
		}
		System.out.println("*Cannot perform any operations on the instance\n");
		return arrIns;

	}
}
