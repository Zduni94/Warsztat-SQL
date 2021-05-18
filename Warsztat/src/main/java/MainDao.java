import java.util.Arrays;

public class MainDao {

    public static void main(String[] args) {
        UserDao userDao = new UserDao();
        userDao.dropDatabase();
        userDao.createDatabase();
        userDao.createUserTable();

        User testUser = new User("test", "test@test", "test");
        System.out.println("Id przed zapsiem: " + testUser.getId());
        testUser = userDao.create(testUser);
        System.out.println("Id po zapisie: " + testUser.getId());

        User sameUser = userDao.read(testUser.getId());
        sameUser.setEmail("test2@test");
        userDao.update(sameUser);
        sameUser.setPassword("abc");
        userDao.updatePassword(sameUser, sameUser.getPassword());
        System.out.println(sameUser);

        User anotherUser = new User("test2", "test3@test3", "test");
        userDao.create(anotherUser);

        User[] users = userDao.findAll();
        System.out.println(Arrays.toString(users));

        User userToDelete = userDao.read(sameUser.getId());
        System.out.println(userToDelete);
        userDao.delete(userToDelete.getId());

        users = userDao.findAll();
        System.out.println(Arrays.toString(users));

        userDao.deleteAll();
        users = userDao.findAll();
        System.out.println(Arrays.toString(users));
    }
}
