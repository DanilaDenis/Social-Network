package domain;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        String exceptions = "";
        //TODO: implement method validate
        if (entity.getFirstName().isEmpty() || entity.getLastName().isEmpty())
            exceptions +="Names cannot be null\n";
        if (entity.getEmail().isEmpty())
            exceptions+="Email cannot be null\n";
        if (entity.getPassword().isEmpty())
            exceptions+="Password cannot be null\n";
        else
            if(entity.getPassword().length() < 8 )
                exceptions+="Password must be at least 8 characters long";
        if(exceptions.length() > 0)
            throw new ValidationException(exceptions);
    }
}
