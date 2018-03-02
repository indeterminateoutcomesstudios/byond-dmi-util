package io.github.spair.byond.dmi;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
import java.util.Base64;
import java.util.Map;

public final class DmiSlurper {

    @Nonnull
    public static Dmi slurpUp(final File dmiFile) {
        try (InputStream input = new FileInputStream(dmiFile)) {
            return slurpUp(dmiFile.getName(), input);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Provided DMI file doesn't exist");
        } catch (IOException e) {
            throw new IllegalArgumentException("Provided DMI can't be read");
        }
    }

    @Nonnull
    public static Dmi slurpUp(final String dmiName, final String base64content) {
        try (InputStream input = new ByteArrayInputStream(Base64.getMimeDecoder().decode(base64content))) {
            return slurpUp(dmiName, input);
        } catch (IOException e) {
            throw new IllegalArgumentException("Provided base64 content can't be read");
        }
    }

    @Nonnull
    public static Dmi slurpUp(final String dmiName, final InputStream input) {
        try (BufferedInputStream bufferedInput = new BufferedInputStream(input)) {
            bufferedInput.mark(input.available());

            BufferedImage dmiImage = ImageIO.read(bufferedInput);
            bufferedInput.reset();

            DmiMeta dmiMeta = MetaExtractor.extractMetadata(bufferedInput);
            Map<String, DmiState> dmiStates = StateExtractor.extractStates(dmiImage, dmiMeta);

            return new Dmi(dmiName, dmiImage.getWidth(), dmiImage.getHeight(), dmiMeta, dmiStates);
        } catch (IOException e) {
            throw new IllegalArgumentException("Provided DMI can't be read");
        }
    }

    private DmiSlurper() {
    }
}
