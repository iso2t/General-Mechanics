package general.api.network.service;

public sealed interface NetworkValue permits NetworkValue.IntValue, NetworkValue.LongValue, NetworkValue.DoubleValue, NetworkValue.BooleanValue, NetworkValue.StringValue {

	record IntValue(int value) implements NetworkValue {
	}

	record LongValue(long value) implements NetworkValue {
	}

	record DoubleValue(double value) implements NetworkValue {
	}

	record BooleanValue(boolean value) implements NetworkValue {
	}

	record StringValue(String value) implements NetworkValue {
	}
}
