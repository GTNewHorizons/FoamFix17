package pl.asie.foamfix.repack.com.unascribed.ears.api;

import static pl.asie.foamfix.repack.com.unascribed.ears.api.EarsAnchorPart.*;

public enum EarsFeatureType {
	EARS(HEAD),
	HORN(HEAD),
	SNOUT(HEAD),
	CLAW_LEFT_ARM(LEFT_ARM),
	CLAW_RIGHT_ARM(RIGHT_ARM),
	CLAW_LEFT_LEG(LEFT_LEG),
	CLAW_RIGHT_LEG(RIGHT_LEG),
	TAIL(TORSO),
	WINGS(TORSO),
	CAPE(TORSO),
	
	OTHER_HEAD(HEAD),
	OTHER_TORSO(TORSO),
	;
	
	private final EarsAnchorPart anchor;
	
	EarsFeatureType(EarsAnchorPart anchor) {
		this.anchor = anchor;
	}
	
	public EarsAnchorPart getAnchor() {
		return anchor;
	}
	
	public boolean isAnchoredTo(EarsAnchorPart part) {
		return anchor == part;
	}
	
	public boolean isAnchoredToAnyArm() {
		return isAnchoredTo(LEFT_ARM) || isAnchoredTo(RIGHT_ARM);
 	}
	
	public boolean isAnchoredToAnyLeg() {
		return isAnchoredTo(LEFT_LEG) || isAnchoredTo(RIGHT_LEG);
 	}
	
}
