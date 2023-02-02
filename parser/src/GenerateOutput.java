import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateOutput {
  private static String title;
  private FileWriter myWriter;

    public GenerateOutput(String suite, String bench, String name, String ext){
      title = name;
      try {
        File f = new File("../output/"+suite+"/"+bench); 

		    // check if the directory can be created 
        // using the specified path name 
        if (f.mkdir() == true) { 
          System.out.println("Directory has been created successfully"); 
        } else { 
          //System.out.println("Directory cannot be created or is already existing"); 
        } 
        File myObj = new File("../output/"+suite+"/"+bench+"/"+name+"."+ext);
        if (myObj.createNewFile()) {
          System.out.println("File created: " + myObj.getName());
          
        } else {
          //System.out.println("File already exists.");
        }
        try {
          myWriter = new FileWriter("../output/"+suite+"/"+bench+"/"+name+"."+ext);
          
          //System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
      } catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }
    }

    public GenerateOutput(String name, String ext){
      title = name;
      try {
        File f = new File("../output/"); 

		    // check if the directory can be created 
        // using the specified path name 
        if (f.mkdir() == true) { 
          System.out.println("Directory has been created successfully"); 
        } else { 
          //System.out.println("Directory cannot be created or is already existing"); 
        } 
        File myObj = new File("../output/"+name+"."+ext);
        if (myObj.createNewFile()) {
          System.out.println("File created: " + myObj.getName());
          
        } else {
          //System.out.println("File already exists.");
        }
        try {
          myWriter = new FileWriter("../output/"+name+"."+ext);
          
          //System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
      } catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }
    }

    public void writeFile(String output) {
      try {
            myWriter.write(output+"\n");
        //System.out.println("Successfully wrote to the file.");
      } catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }
    }

  public String getName(){
    return title;
  } 

    public void writeFileRow(String output) {
      try {
            myWriter.write(output);
        //System.out.println("Successfully wrote to the file.");
      } catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }
    }

    public void closeFile(){
      try {
        myWriter.close();
        //System.out.println("Successfully file closed.");
      } catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }
      
    }
    
}
