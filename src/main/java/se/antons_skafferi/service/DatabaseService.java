// DatabaseService.java
package se.antons_skafferi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.antons_skafferi.dataClass.*;
import se.antons_skafferi.repository.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class DatabaseService {

    @Autowired
    private LunchRepository lunchRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private DinnerRepository dinnerRepository;
    @Autowired
    private EventsRepository eventsRepository;
    @Autowired
    private TabRepository tabRepository;
    @Autowired
    private DrinksRepository drinksRepository;
    @Autowired
    private TablesRepository tablesRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private FoodOrderRepository foodOrderRepository;
    @Autowired
    private DrinkOrderRepository drinkOrderRepository;
    @Autowired
    private ItemsRepository itemsRepository;



    /**
     * Get all menu items
     * @return List of all menu items
     */



    // PERSON RELATED METHODS ##########################################
    // GET -----------------
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }
    // POST -----------------
// DatabaseService.java
    public Person addPerson(Person person) {
        if (personRepository.existsByEmail(person.getEmail())) {
            throw new IllegalArgumentException("Invalid email: Email already exists");
        }
        if (personRepository.existsByPhoneNumber(person.getPhone_number())) {
            throw new IllegalArgumentException("Invalid phone number: Phone number already exists");
        }
        return personRepository.save(person);
    }

    // ITEMS ############################################################
    // GET -----------------
    public List<Items> getAllItems() {
        return itemsRepository.findAll();
    }

    // POST -----------------
    public Items addItem(Items item) {
        return itemsRepository.save(item);
    }





    // LUNCH ##########################################
    // GET -----------------
    public List<Lunch> getAllLunchItems() {
        return lunchRepository.findAllWithItems();
    }

    public List<Lunch> getLunchItemsByDate(Date date) {
        return lunchRepository.findByDate(date);
    }

    public List<Lunch> getLunchItemsByWeekAndYear(int week, int year) {
        return lunchRepository.findByWeekAndYear(week, year);
    }

    // POST -----------------
    public Lunch addLunchItem(Lunch lunch) {
        return lunchRepository.save(lunch);
    }
    public Lunch addItemsToLunch(Integer lunchId, List<Integer> itemIds) {
        Lunch lunch = lunchRepository.findById(lunchId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid lunch ID"));

        for (Integer itemId : itemIds) {
            Items item = itemsRepository.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid item ID"));
            LunchItems lunchItem = new LunchItems();
            lunchItem.setLunch(lunch);
            lunchItem.setItem(item);
            lunch.getLunchItems().add(lunchItem);
        }

        return lunchRepository.save(lunch);
    }

    // DINNER ############################################################
    // GET -----------------
    public List<Dinner> getAllDinnerItems() {
        return dinnerRepository.findAll();
    }
    public List<Dinner> getDinnerItemsByType(String type) {
        return dinnerRepository.findByType(type);
    }
    // POST -----------------
    public Dinner addDinnerItem(Dinner dinner) {
        return dinnerRepository.save(dinner);
    }



    // BOOKINGS ############################################################
    public List<Bookings> getBookings() {
        return bookingRepository.findAll();
    }

    public List<Bookings> getBookingsByDate(Date date) {
        return bookingRepository.findByDate(date);
    }
    public List<Bookings> getBookingsByWeekAndYear(int week, int year) {
        return bookingRepository.findByWeekAndYear(week, year);
    }

    public Bookings addBooking(Bookings booking) {
        if (booking.getPerson() == null) {
            throw new IllegalArgumentException("Person information is missing");
        }
        if (booking.getPerson().getPerson_id() == null) {
            throw new IllegalArgumentException("Person ID is missing");
        }
        Person person = personRepository.findById(booking.getPerson().getPerson_id())
                .orElseThrow(() -> new IllegalArgumentException("Invalid person ID"));
        booking.setPerson(person);
        return bookingRepository.save(booking);
    }

    public Bookings updateBookingStatus(Integer id, Bookings.Status status) {
        Bookings booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid booking ID"));
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }



    // Events   ############################################################
    // GET -----------------
    public List<Events> getAllEvents() {
        return eventsRepository.findAll();
    }

    public List<Events> getEventsByWeekAndYear(int week, int year) {
        return eventsRepository.findByWeekAndYear(week, year);
    }

    // Get events by date
    public List<Events> getEventsByDate(Date date) {
        return eventsRepository.findByDate(date);
    }

    // POST -----------------
    public Events addEvent(Events event) {
        return eventsRepository.save(event);
    }


    // Orders ############################################################
    // GET -----------------
    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }

    public Orders getOrdersById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));
    }

    public List<Orders> getOrdersByDate(Date date) {
        return orderRepository.findByDate(date);
    }

    public List<Orders> getOrdersByStatus(Orders.Status status) {
        return orderRepository.findByStatus(status);
    }

    // POST -----------------

    public Orders addOrder(Orders order) {
        return orderRepository.save(order);
    }


    public Orders addMenuItemToOrder(int orderId, Integer dinnerId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));
        Dinner dinner = dinnerRepository.findById(dinnerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid dinner ID"));

        Optional<FoodOrder> optionalFoodOrder = foodOrderRepository.findByOrderAndDinner(order, dinner);
        FoodOrder foodOrder = optionalFoodOrder.orElseGet(() -> {
            FoodOrder newFoodOrder = new FoodOrder();
            newFoodOrder.setOrder(order);
            newFoodOrder.setDinner(dinner);
            newFoodOrder.setQuantity(0);
            return newFoodOrder;
        });

        foodOrder.setQuantity(foodOrder.getQuantity() + 1);
        foodOrderRepository.save(foodOrder);

        return orderRepository.save(order);
    }

    public Orders addDrinkToOrder(int orderId, Integer drinkId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));
        Drinks drink = drinksRepository.findById(drinkId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid drink ID"));

        Optional<DrinkOrder> optionalDrinkOrder = drinkOrderRepository.findByOrderAndDrink(order, drink);
        DrinkOrder drinkOrder = optionalDrinkOrder.orElseGet(() -> {
            DrinkOrder newDrinkOrder = new DrinkOrder();
            newDrinkOrder.setOrder(order);
            newDrinkOrder.setDrink(drink);
            newDrinkOrder.setQuantity(0);
            return newDrinkOrder;
        });

        drinkOrder.setQuantity(drinkOrder.getQuantity() + 1);
        drinkOrderRepository.save(drinkOrder);

        return orderRepository.save(order);
    }

    public Orders updateOrderStatus(int orderId, Orders.Status status) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));
        order.setStatus(status);
        return orderRepository.save(order);
    }





    // Tabs ############################################################
    // GET -----------------
    public List<Tab> getAllTabs() {
        return tabRepository.findAll();
    }

    public Tab getTabById(Integer id) {
        return tabRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tab ID"));
    }

    public List<Tab> getTabByDate(Timestamp date) {
        return tabRepository.findByOpened_at(date);
    }

    public List<Tab> getTabByStatus(Tab.Status status) {
        return tabRepository.findByStatus(status);
    }

    public List<Tab> getTabByTable(Integer table) {
        return tabRepository.findByTable_number(table);
    }

    // POST -----------------
    public Tab addTab(Tab tab) {
        return tabRepository.save(tab);
    }
    public Tab updateTabStatus(int tabId, Tab.Status status) {
        Tab tab = tabRepository.findById(tabId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tab ID"));
        tab.setStatus(status);
        return tabRepository.save(tab);
    }



    // Drinks ############################################################
    // GET -----------------
    public List<Drinks> getAllDrinks() {
        return drinksRepository.findAll();
    }

    public Drinks getDrinkById(int id) {
        return drinksRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid drink ID"));
    }

    public List<Drinks> getDrinksByType(String type) {
        return drinksRepository.findByType(type);
    }


    // POST -----------------

    public Drinks addDrink(Drinks drink) {
        return drinksRepository.save(drink);
    }

    public Drinks updateDrinkPrice(int drinkId, Integer price) {
        Drinks drink = drinksRepository.findById(drinkId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid drink ID"));
        drink.setPrice(price);
        return drinksRepository.save(drink);
    }



    // Tables   ############################################################
    // GET -----------------
    public List<Tables> getAllTables() {
        return tablesRepository.findAll();
    }

    public Tables getTableById(int id) {
        return tablesRepository.findById(id);
    }

    public List<Tables> getTablesByNumberOfSeats(int room_for_people) {
        return tablesRepository.findByRoom_for_people(room_for_people);
    }

    // POST -----------------
    public Tables addTable(Tables table) {
        return tablesRepository.save(table);
    }





    // Employees    ############################################################
    // GET -----------------
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(int id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));
    }

    public List<Employee> getEmployeesByName(String name) {
        return employeeRepository.findByName(name);
    }

    public String getEmployeePasswordByEmail(String email) {
        return employeeRepository.findPasswordByEmail(email);
    }

    public List<Role> getHierarchyByEmployeeId(int employee_id) {
        return employeeRepository.findHierarchyByEmployeeId(employee_id);
    }

    // POST -----------------
    public Employee addEmployee(Employee employee) {
        Person person = employee.getPerson(); // Check if person exists
        if (person.getPerson_id() == null || !personRepository.existsById(person.getPerson_id())) {
            throw new IllegalArgumentException("Invalid person ID");
        }

        // Check if person ID is already associated with an existing employee
        if (employeeRepository.existsByPersonId(person.getPerson_id())) {
            throw new IllegalArgumentException("Person ID is already in use");
        }

        Role role = employee.getRole(); // Check if role exists
        if (role.getRole_id() == null || !roleRepository.existsById(role.getRole_id())) {
            throw new IllegalArgumentException("Invalid role ID");
        }

        return employeeRepository.save(employee);
    }
    // Update employee password
    public Employee updateEmployeePassword(int employeeId, String password) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));
        employee.setPassword(password);
        return employeeRepository.save(employee);
    }

    // Change employee role
    public Employee changeEmployeeRole(int employeeId, int roleId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID"));

        employee.setRole(role);
        return employeeRepository.save(employee);
    }



    // Roles
    // GET -----------------
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(int id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID"));
    }

    // POST -----------------
    // Add a new role
    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    // Update the hierarchy of a role
    public Role updateRoleHierarchy(int roleId, Integer hierarchyLevel) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID"));
        role.setHierarchy_level(hierarchyLevel);
        return roleRepository.save(role);
    }



}