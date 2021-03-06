package io.fundrequest.azrael.worker.contracts;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class FundRequestContract extends Contract {
    public FundRequestContract(final String contractBinary, final String contractAddress, final Web3j web3j, final TransactionManager transactionManager, final BigInteger gasPrice, final BigInteger gasLimit) {
        super(contractBinary, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public FundRequestContract(final String contractBinary, final String contractAddress, final Web3j web3j, final Credentials credentials, final BigInteger gasPrice, final BigInteger gasLimit) {
        super(contractBinary, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public FundRequestContract(final String contractAddress, final Web3j web3j, final TransactionManager transactionManager, final BigInteger gasPrice, final BigInteger gasLimit) {
        super(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public FundRequestContract(final String contractAddress, final Web3j web3j, final Credentials credentials, final BigInteger gasPrice, final BigInteger gasLimit) {
        super(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public Uint256 getBalance(byte[] data) throws ExecutionException, InterruptedException {
        Function function = new Function("balance",
                Arrays.asList(new org.web3j.abi.datatypes.generated.Bytes32(data)),
                Collections.singletonList(new TypeReference<Uint256>() {
                })
        );
        return executeCallSingleValueReturn(function);
    }

    public String getBalance(final String data) throws ExecutionException, InterruptedException {
        return getBalance(Arrays.copyOf(data.getBytes(), 32)).getValue().toString();
    }

    public Optional<EventValues> getEventParameters(
            Event event, Log log) {

        final List<String> topics = log.getTopics();
        final String encodedEventSignature = EventEncoder.encode(event);
        if (!topics.get(0).equals(encodedEventSignature)) {
            return Optional.empty();
        }

        final List<Type> indexedValues = new ArrayList<>();
        final List<Type> nonIndexedValues = FunctionReturnDecoder.decode(
                log.getData(), event.getNonIndexedParameters());

        final List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value = FunctionReturnDecoder.decodeIndexedValue(
                    topics.get(i + 1), indexedParameters.get(i));
            indexedValues.add(value);
        }
        return Optional.of(new EventValues(indexedValues, nonIndexedValues));
    }
}
