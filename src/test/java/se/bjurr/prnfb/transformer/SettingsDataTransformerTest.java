package se.bjurr.prnfb.transformer;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.prnfb.transformer.ButtonTransformer.toButtonDto;
import static se.bjurr.prnfb.transformer.ButtonTransformer.toPrnfbButton;

import org.junit.Test;

import se.bjurr.prnfb.presentation.dto.ButtonDTO;
import se.bjurr.prnfb.settings.ValidationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class SettingsDataTransformerTest {
 @Test
 public void testTransformation() throws ValidationException {
  PodamFactory factory = new PodamFactoryImpl();
  ButtonDTO originalDto = factory.manufacturePojo(ButtonDTO.class);
  ButtonDTO retransformedDto = toButtonDto(toPrnfbButton(originalDto));

  assertThat(retransformedDto)//
    .isEqualTo(originalDto);
 }

}
