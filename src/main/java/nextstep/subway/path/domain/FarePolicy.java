package nextstep.subway.path.domain;

import java.util.Comparator;
import java.util.List;

import nextstep.subway.line.domain.Line;

public class FarePolicy {
	private static final int BASIC_FARE = 1250;

	public int calculateBy(int distance) {
		if (!OverFareSection.contains(distance)) {
			return BASIC_FARE;
		}

		OverFareSection overFareSection = OverFareSection.findBy(distance);
		int totalOverFare = overFareSection.calculateTotalOverFare(distance);
		return BASIC_FARE + totalOverFare;
	}

	public int calculateBy(int distance, List<Line> lines) {
		Integer maxExtraFare = lines.stream()
			.map(Line::getExtraFare)
			.max(Comparator.comparingInt(o -> o))
			.orElseThrow(IllegalStateException::new);

		return calculateBy(distance) + maxExtraFare;
	}

	public int calculateBy(int distance, List<Line> lines, int age) {
		int fare = calculateBy(distance, lines);
		if (!FareDiscountAge.contains(age)) {
			return fare;
		}

		FareDiscountAge fareDiscountAge = FareDiscountAge.findBy(age);
		return fareDiscountAge.getDiscountFare(fare);
	}
}
