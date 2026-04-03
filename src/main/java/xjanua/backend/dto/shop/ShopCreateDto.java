package xjanua.backend.dto.shop;

import java.util.Optional;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopCreateDto {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 65535, message = "Description must be less than 65535 characters")
    private String description;

    // @NotBlank(message = "Old Province ID is required")
    // @Size(max = 50, message = "Old Province ID must be less than 50 characters")
    private Optional<String> oldProviceId;

    // @NotBlank(message = "Old District ID is required")
    // @Size(max = 50, message = "Old District ID must be less than 50 characters")
    private Optional<String> oldDistrictId;

    // @NotBlank(message = "Old Ward ID is required")
    // @Size(max = 50, message = "Old Ward ID must be less than 50 characters")
    private Optional<String> oldWardId;

    // @NotBlank(message = "New Province ID is required")
    // @Size(max = 50, message = "New Province ID must be less than 50 characters")
    private Optional<String> newProviceId;

    // @NotBlank(message = "New Ward ID is required")
    // @Size(max = 50, message = "New Ward ID must be less than 50 characters")
    private Optional<String> newWardId;

    // @NotBlank(message = "Street Line is required")
    // @Size(max = 255, message = "Street Line must be less than 255 characters")
    private Optional<String> streetLine;

    // @NotBlank(message = "Full Old Address is required")
    // @Size(max = 500, message = "Full Old Address must be less than 500
    // characters")
    private Optional<String> fullOldAddress;

    // @NotBlank(message = "Full New Address is required")
    // @Size(max = 500, message = "Full New Address must be less than 500
    // characters")
    private Optional<String> fullNewAddress;
}
