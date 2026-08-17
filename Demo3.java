class CustomException extends Exception{
    public CustomException(String string){
        super(string);
    }
}

public class Demo3 {
    public static void main(String[] args) {
        int i = 0;

        try {
            if(i == 0) {
                throw new CustomException("The is zero so please handle it properly");
            }

            i = 10 / i;

        } catch(CustomException e){
            System.out.println("Custom Exception: " + e);
        } 
        catch (ArithmeticException e) {
            System.out.println("Arithmetic Exception: " + e);
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }
    }
}
