public class Customer {

    private String customerId;
    private String name;
    private String contact;
    private String email;
    private String idType;    // e.g. Passport, Driver's License …
    private String idNumber;

    // accepted id's
    public static final String[] ID_TYPES = {
        "Passport", "Driver's License", "SSS Card",
        "PhilHealth ID", "TIN ID", "National ID"
    };

    // constructor 
    public Customer(String customerId, String name, String contact,
                    String email, String idType, String idNumber) {
        this.customerId = customerId;
        this.name       = name;
        this.contact    = contact;
        this.email      = email;
        this.idType     = idType;
        this.idNumber   = idNumber;
    }

    // getters
    public String getCustomerId() { return customerId; }
    public String getName()       { return name;       }
    public String getContact()    { return contact;    }
    public String getEmail()      { return email;      }
    public String getIdType()     { return idType;     }
    public String getIdNumber()   { return idNumber;   }

    // setters
    public void setName(String name)         { this.name     = name;     }
    public void setContact(String contact)   { this.contact  = contact;  }
    public void setEmail(String email)       { this.email    = email;    }
    public void setIdType(String idType)     { this.idType   = idType;   }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    // saving files
    public String toFileString() {
        return customerId + "|" + name + "|" + contact + "|"
               + email + "|" + idType + "|" + idNumber;
    }

    public static Customer fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        return new Customer(p[0], p[1], p[2], p[3], p[4], p[5]);
    }

    @Override
    public String toString() {
        return "[" + customerId + "] " + name;
    }
}
