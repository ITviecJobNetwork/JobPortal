package com.fashion.constant;

import com.fashion.dto.user.UserResponse;
import com.fashion.entity.Order;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static com.fashion.constant.OrderStatus.UpdateStatusCondition.*;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {
    PENDING("Chờ duyệt", nonCondition()),
    REJECT("Từ chối", statusAndAdmin(PENDING)),
    CANCEL("Hủy bỏ", statusAndCreator(PENDING)),
    APPROVED("Phê duyệt", statusAndAdmin(PENDING)),
    DELIVERING("Đang vận chuyển", statusAndAdmin(APPROVED)),
    DONE("Hoàn thành", statusAndAdmin(DELIVERING)),
    ;

    private final String value;
    private final UpdateStatusCondition condition;


    @FunctionalInterface
    public interface UpdateStatusCondition {

        boolean test(Order order, UserResponse userResponse);

        static UpdateStatusCondition nonCondition() {
            return (o, u) -> true;
        }

        static UpdateStatusCondition statusAndCreator(OrderStatus orderStatus) {
            return (order, currentUser) -> order.getStatus().equals(orderStatus) && order.getCreatedBy().equals(currentUser.getEmail());
        }

        static UpdateStatusCondition statusAndAdmin(OrderStatus oldStatus) {
            return (order, userResponse) -> order.getStatus().equals(oldStatus) && RoleConstant.ADMIN.equals(userResponse.getRole());
        }
    }
}