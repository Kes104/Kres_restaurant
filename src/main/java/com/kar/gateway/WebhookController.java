package com.kar.gateway;

import com.kar.billing.BillRepository;
import com.kar.billing.BillingService;

import com.kar.chef.*;
import com.kar.menu.DishRepository;
import com.kar.order.OrderEvent;
import com.kar.order.OrderEventProducer;
import com.kar.session.DiningSession;
import com.kar.table.RestaurantTable;
import com.kar.table.TableService;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import com.kar.session.SessionService;
import com.kar.menu.MenuService;
import com.kar.order.OrderService;

@Controller("/webhook")
public class WebhookController {

    private final SessionService sessionService;
    private final MenuService menuService;
    private final TelegramService telegramService;
    private final OrderService orderService;
    private final TableService tableService;
    private final ChefOrderRepository chefOrderRepository;
    private String currentOwnerOtp = null;
    private final ChefRepository chefRepository;
    private final BillingService billingService;
    private final BillRepository billRepository;
    private final ChefService chefService;
    private final ChefOrderItemRepository chefOrderItemRepository;
    private final DishRepository dishRepository;
    private final OrderEventProducer orderEventProducer;

    public WebhookController(SessionService sessionService,
                             MenuService menuService,
                             TelegramService telegramService,
                             OrderService orderService,
                             TableService tableService,
                             ChefOrderRepository chefOrderRepository,
                             ChefOrderItemRepository chefOrderItemRepository,
                             ChefRepository chefRepository,
                             BillRepository billRepository,
                             BillingService billingService,
                             ChefService chefService,
                             DishRepository dishRepository,
                             OrderEventProducer orderEventProducer) {
        this.sessionService = sessionService;
        this.menuService = menuService;
        this.telegramService = telegramService;
        this.orderService = orderService;
        this.tableService = tableService;
        this.chefOrderRepository = chefOrderRepository;
        this.chefRepository = chefRepository;
        this.chefOrderItemRepository = chefOrderItemRepository;
        this.billingService = billingService;
        this.billRepository = billRepository;
        this.chefService = chefService;
        this.dishRepository = dishRepository;
        this.orderEventProducer = orderEventProducer;
    }

    private String getEstimatedTime(Long categoryId) {
        if (categoryId == 1) return "15 minutes";      // Breakfast
        else if (categoryId == 2) return "25 minutes"; // Lunch
        else if (categoryId == 3) return "10 minutes"; // Snacks
        return "20 minutes";
    }

    @Post("/telegram")
    public void handleUpdate(@Body TelegramUpdate update) {
        if (update.getMessage() == null) return;

        Long chatId = update.getMessage().getFrom().getId();
        String firstName = update.getMessage().getFrom().getFirstName();
        String text = update.getMessage().getText();

        if (text == null) return;

        if (text.equals("/start")) {
            sessionService.findOrCreateCustomerSession(chatId, firstName);
            telegramService.sendMessage(chatId,
                    "Welcome to Kres Restaurant, " + firstName + "! 🍽\n" + "Type /table to book a table\n" +
                            "Type /menu to see our menu\n" +
                            "Type /bill to request your bill");

        } else if (text.equals("/table")) {
            var sessions = sessionService.getActiveSessionByTelegramId(chatId);
            if (sessions.isEmpty()) {
                telegramService.sendMessage(chatId,
                        "Please type /start first.");
                return;
            }
            var session = sessions.get();
            if (session.getTableId() != null) {
                telegramService.sendMessage(chatId,
                        "You are already seated at table " + session.getTableId() +
                                ".\nType /menu to order.");
                return;
            }
            telegramService.sendMessage(chatId,
                    "How many people in your party?\nReply: table <number>\nExample: table 3");

        } else if (text.toLowerCase().startsWith("table ")) {
            String[] parts = text.split(" ");
            int partySize;
            try {
                partySize = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                telegramService.sendMessage(chatId,
                        "Please enter a valid number.\nExample: table 3");
                return;
            }
            var sessions = sessionService.getActiveSessionByTelegramId(chatId);
            if (sessions.isEmpty()) {
                telegramService.sendMessage(chatId,
                        "Please type /start first.");
                return;
            }
            var session = sessions.get();
            var table = tableService.assignTable(partySize);
            if (table == null) {
                telegramService.sendMessage(chatId,
                        "Sorry, no tables available for " + partySize +
                                " people right now. Please wait...");
                return;
            }
            sessionService.assignTableToSession(session.getId(), table.getId());
            telegramService.sendMessage(chatId,
                    "✅ Table assigned!\n" +
                            "Table number: " + table.getId() + "\n" +
                            "Seats: " + table.getSize() + "\n\n" +
                            "⏱ Current kitchen wait time: ~15\n\n" +
                            "Type /menu to see our menu and start ordering.");
        } else if (text.equals("/chef")) {
            telegramService.sendMessage(chatId,
                    "Enter your chef ID:\nReply: chef <id>\nExample: chef 1");

        } else if (text.toLowerCase().startsWith("chef ")) {
            String[] parts = text.split(" ");
            Long chefId;
            try {
                chefId = Long.parseLong(parts[1]);
            } catch (NumberFormatException e) {
                telegramService.sendMessage(chatId,
                        "Invalid chef ID. Example: chef 1");
                return;
            }
            var chefOpt = chefRepository.findById(chefId);
            if (chefOpt.isEmpty()) {
                telegramService.sendMessage(chatId,
                        "Chef not found. Please check your ID.");
                return;
            }
            var chef = chefOpt.get();
            var chefOrders = chefOrderRepository.findByChefId(chefId);
            StringBuilder sb = new StringBuilder();
            sb.append("👨‍🍳 Chef: ").append(chef.getName()).append("\n\n");
            sb.append("📋 Pending Orders:\n");
            boolean hasOrders = false;
            for (var order : chefOrders) {
                if (order.getStatus() != ChefOrder.sta.COMPLETED) {
                    sb.append("• Order ID: ").append(order.getOrderId())
                            .append(" — ").append(order.getStatus())
                            .append(" [Priority: ").append(order.getPriority()).append("]")
                            .append("\n")
                            .append("  ✅ To complete: complete ").append(order.getId())
                            .append("\n");
                    hasOrders = true;
                }
            }
            if (!hasOrders) {
                sb.append("No pending orders right now.");
            }
            telegramService.sendMessage(chatId, sb.toString());
        } else if (text.equals("/menu")) {
            var categories = menuService.getAllCategories();
            StringBuilder sb = new StringBuilder("Our Menu:\n\n");
            for (var category : categories) {
                sb.append("📌 ").append(category.getName()).append("\n");
                var dishes = menuService.getDishesByCategoryId(category.getId());
                for (var dish : dishes) {
                    sb.append("  • ").append(dish.getName())
                            .append(" — ₹").append(dish.getPrice()).append("\n");
                }
                sb.append("\n");
            }
            telegramService.sendMessage(chatId, sb.toString());

        } else if (text.toLowerCase().startsWith("complete ")) {
            String[] parts = text.split(" ");
            Long chefOrderId;
            try {
                chefOrderId = Long.parseLong(parts[1]);
            } catch (NumberFormatException e) {
                telegramService.sendMessage(chatId,
                        "Invalid order ID. Example: complete 1");
                return;
            }
            var chefOrderOpt = chefOrderRepository.findById(chefOrderId);
            if (chefOrderOpt.isEmpty()) {
                telegramService.sendMessage(chatId,
                        "Chef order not found.");
                return;
            }
            var chefOrder = chefOrderOpt.get();

            // Get dish prep time
            var chefOrderItems = chefOrderItemRepository.findByChefOrderId(chefOrderId);
            int prepTime = 0;
            for (var item : chefOrderItems) {
                var dishOpt = dishRepository.findById(item.getDishId());
                if (dishOpt.isPresent() && dishOpt.get().getPrepTime() != null) {
                    prepTime += dishOpt.get().getPrepTime();
                }
            }

            // Update chef order status
            chefService.updateChefOrderStatus(chefOrderId,
                    ChefOrder.sta.COMPLETED);

            // Reduce chef remaining time
            chefService.reduceChefTime(chefOrder.getChefId(), prepTime);

            // Trigger sync check
            OrderEvent event = new OrderEvent();
            event.setOrderId(chefOrder.getOrderId());
            event.setChefId(chefOrder.getChefId());
            event.setPriority("READY");
            Thread.ofVirtual().start(() ->
                    orderEventProducer.sendToNotification(event).subscribe());

            telegramService.sendMessage(chatId,
                    "✅ Order " + chefOrderId + " marked as completed!");
        }
        else if (text.equals("/bill")) {
        var sessions = sessionService.getActiveSessionByTelegramId(chatId);
        if (sessions.isEmpty()) {
            telegramService.sendMessage(chatId,
                    "No active session found. Type /start first.");
            return;
        }
        var session = sessions.get();
        if (session.getStatus() == DiningSession.stat.CLOSED) {
            telegramService.sendMessage(chatId,
                    "Your session is already closed.");
            return;
        }
        var bill = billingService.generateBill(session.getId());
        var table = tableService.getTableById(session.getTableId());
        if (table != null) {
            table.setStatus(RestaurantTable.TableStatus.AVAILABLE);
            tableService.updateTableStatus(table.getId(),
                    RestaurantTable.TableStatus.AVAILABLE);
        }
        telegramService.sendMessage(chatId,
                "🧾 *Your Bill*\n\n" +
                        "Total: ₹" + bill.getTotalAmount() + "\n" +
                        "GST (5%): ₹" + bill.getTaxAmount() + "\n" +
                        "─────────────\n" +
                        "Final Amount: ₹" + bill.getFinalAmount() + "\n\n" +
                        "Thank you for dining with Kres Restaurant! 🙏\n" +
                        "Your table has been released.");
    } else if (text.equals("/clear")) {
        var sessions = sessionService.getActiveSessionByTelegramId(chatId);
            sessions.ifPresent(diningSession -> sessionService.updateSessionStatus(diningSession.getId(), DiningSession.stat.CLOSED));
        telegramService.sendMessage(chatId,
                "✅ Your session has been cleared.\nType /start to begin a new order.");
        } else if (text.toLowerCase().startsWith("order ")) {
            // Format: "order <dish name> <quantity>"
            String[] parts = text.split(" ");
            if (parts.length < 3) {
                telegramService.sendMessage(chatId,
                        "Format: order <dish name> <quantity>\nExample: order Idli 2");
                return;
            }
            int quantity;
            try {
                quantity = Integer.parseInt(parts[parts.length - 1]);
            } catch (NumberFormatException e) {
                telegramService.sendMessage(chatId,
                        "Please specify quantity at the end.\nExample: order Idli 2");
                return;
            }

            // Build dish name from middle parts
            StringBuilder dishName = new StringBuilder();
            for (int i = 1; i < parts.length - 1; i++) {
                if (i > 1) dishName.append(" ");
                dishName.append(parts[i]);
            }

            var dishes = menuService.findByName(dishName.toString());
            if (dishes.isEmpty()) {
                telegramService.sendMessage(chatId,
                        "Sorry, dish '" + dishName + "' not found. Type /menu to see available dishes.");
                return;
            }

            var dish = dishes.getFirst();
            var sessions = sessionService.getActiveSessionByTelegramId(chatId);
            if (sessions.isEmpty()) {
                telegramService.sendMessage(chatId,
                        "No active session found. Please type /start first.");
                return;
            }

            var session = sessions.get();
            var orders = orderService.getOrdersBySessionId(session.getId());
            Long orderId;
            if (!orders.iterator().hasNext()) {
                var newOrders = orderService.createOrder(session.getId());
                orderId = newOrders.iterator().next().getId();
            } else {
                orderId = orders.iterator().next().getId();
            }

            orderService.addItemtoOrder(orderId, dish.getId(), quantity);
            var category = menuService.getCategoryById(dish.getCategoryId());
            int waitTime = 0;
            if (category.isPresent()) {
                waitTime = chefService.getEstimatedWaitTime(
                        category.get().getChefId(), dish.getPrepTime());
            }
            telegramService.sendMessage(chatId,
                    "✅ Added to your order: " + dish.getName() + " x" + quantity + "\n" +
                            "⏱ Estimated wait time: " + waitTime + " minutes\n\n" +
                            "Type more orders or /bill to request your bill.");
        } else if (text.equals("/owner")) {
        // Generate 6 digit OTP
        currentOwnerOtp = String.valueOf((int)(Math.random() * 900000) + 100000);
        System.out.println("=== OWNER OTP: " + currentOwnerOtp + " ===");
        telegramService.sendMessage(chatId,
                "OTP has been generated.\nCheck the server logs and reply:\nowner <otp>");

    } else if (text.toLowerCase().startsWith("owner ")) {
        String[] parts = text.split(" ");
        String enteredOtp = parts[1];
        if (!enteredOtp.equals(currentOwnerOtp)) {
            telegramService.sendMessage(chatId,
                    "Invalid OTP. Type /owner to generate a new one.");
            return;
        }
        currentOwnerOtp = null; // invalidate after use
        var allTables = tableService.getAllTables();
        long occupied = 0, available = 0;
        for (var table : allTables) {
            if (table.getStatus() == RestaurantTable.TableStatus.OCCUPIED) occupied++;
            else available++;
        }
        var allBills = billRepository.findAll();
        double totalIncome = 0.0;
        long totalOrders = 0;
        for (var bill : allBills) {
            totalIncome += bill.getFinalAmount();
            totalOrders++;
        }
        telegramService.sendMessage(chatId,
                "👑 Owner Dashboard\n\n" +
                        "🪑 Tables Occupied: " + occupied + "\n" +
                        "✅ Tables Available: " + available + "\n\n" +
                        "📦 Total Bills Generated: " + totalOrders + "\n" +
                        "💰 Total Income: ₹" + totalIncome + "\n\n" +
                        "Active Chefs:\n" +
                        "• Chef Ravi (Breakfast)\n" +
                        "• Chef Lakshmi (Lunch)\n" +
                        "• Chef Arjun (Snacks — 4:30PM to 9:30PM)");
    }
        else {
            telegramService.sendMessage(chatId,
                    "I didn't understand that. Type /menu to see our menu.");
        }
    }
}