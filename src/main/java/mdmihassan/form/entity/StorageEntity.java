package mdmihassan.form.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "storage_entities")
public class StorageEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(nullable = false, length = 512)
    private String signature;

    @Column(nullable = false)
    private String fileName;

    @Enumerated(EnumType.STRING)
    private Extension extension;

    private String mimeType;

    private Long fileSize;

    @Column(length = 512)
    private String checksum;

    private boolean deleted;

    private Timestamp uploadedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public enum Extension {
        MP4("mp4"),
        AVI("avi"),
        MOV("mov"),
        WMV("wmv"),
        MKV("mkv"),
        FLV("flv"),
        MPEG("mpeg"),
        WEBM("webm"),
        GP("3gp"),
        OGV("ogv"),
        JPEG("jpeg"),
        PNG("png"),
        GIF("gif"),
        BMP("bmp"),
        TIFF("tiff"),
        SVG("svg"),
        RAW("raw"),
        PSD("psd"),
        AI("ai"),
        EPS("eps"),
        TORRENT("torrent");

        private final String name;

        Extension(String name) {
            this.name = name;
        }

        public static Extension of(String extension) {
            return switch (extension.toLowerCase()) {
                case "mp4" -> MP4;
                case "avi" -> AVI;
                case "mov" -> MOV;
                case "wmv" -> WMV;
                case "mkv" -> MKV;
                case "flv" -> FLV;
                case "mpeg" -> MPEG;
                case "webm" -> WEBM;
                case "3gp" -> GP;
                case "ogv" -> OGV;
                case "jpeg", "jpg" -> JPEG;
                case "png" -> PNG;
                case "gif" -> GIF;
                case "bmp" -> BMP;
                case "tiff", "tif" -> TIFF;
                case "svg" -> SVG;
                case "raw" -> RAW;
                case "psd" -> PSD;
                case "ai" -> AI;
                case "eps" -> EPS;
                case "torrent" -> TORRENT;
                default -> throw new IllegalArgumentException("Unsupported extension: " + extension);
            };
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
