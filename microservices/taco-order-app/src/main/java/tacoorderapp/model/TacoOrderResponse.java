package tacoorderapp.model;

import tacoorderapp.dto.AddressDto;

import java.util.List;

public class TacoOrderResponse {

    private List<Taco> tacos;
    private String accountNumber;
    private AddressDto address;

    public TacoOrderResponse(List<Taco> tacos, String accountNumber, AddressDto address) {
        this.tacos = tacos;
        this.accountNumber = accountNumber;
        this.address = address;
    }

    // getters and setters
    public List<Taco> getTacos() { return tacos; }
    public void setTacos(List<Taco> tacos) { this.tacos = tacos; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public AddressDto getAddress() { return address; }
    public void setAddress(AddressDto address) { this.address = address; }
}
