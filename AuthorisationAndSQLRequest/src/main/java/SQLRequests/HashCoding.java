package SQLRequests;

public class HashCoding {


    public int GetHashCode(String pass) {
        int result = pass.hashCode();
        return result;
    }

    public int GetHashCodeSalt(int hashPassword) {
        int result = hashPassword * 12;
        return result;
    }


}
