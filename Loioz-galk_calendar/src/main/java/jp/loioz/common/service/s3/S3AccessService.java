package jp.loioz.common.service.s3;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;;

@Service
public class S3AccessService {

	/** ポート */
	@Value("${s3.bucket-name}")
	private String bucketName;

	/** エンドポイント */
	@Value("${s3.endpoint-url}")
	private String endpointUrl;

	/** リージョン */
	@Value("${s3.region}")
	private String region;

	/** アクセスキー */
	@Value("${s3.access-key}")
	private String accessKey;

	/** シークレットキー */
	@Value("${s3.secret-key}")
	private String secretKey;

	/** パスアクセス有効 */
	@Value("${s3.path-style-access-enabled}")
	private boolean pathStyleAccessEnabled;

	/** パートサイズを5MBで設定 */
	private final int defaultPartSize = 5 * 1024 * 1024;

	// =========================================================================
	// public メソッド
	// =========================================================================
	// --------------------------------------------------
	// アップロード
	// --------------------------------------------------
	/**
	 * ファイルアップロード処理（通常アップロード）
	 * ※プライベートオブジェクト
	 * 
	 * @param objectKey
	 * @param contentType
	 * @param is
	 * @throws Exception
	 */
	public void putPrivateObject(String objectKey, String contentType, InputStream is) throws Exception {

		// クライアント生成
		AmazonS3 client = getClient();

		// メタデータ
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(contentType);

		// リクエスト情報
		PutObjectRequest putRequest = this.getPutPrivateObjectRequest(objectKey, is, metadata);

		// アップロード
		client.putObject(putRequest);
	}

	/**
	 * ファイルアップロード処理（通常アップロード）
	 * ※パブリックオブジェクト
	 * 
	 * @param objectKey
	 * @param contentType
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public String putPublicObject(String objectKey, String contentType, InputStream is) throws Exception {

		// クライアント生成
		AmazonS3 client = getClient();

		// メタデータ
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(contentType);

		// リクエスト情報
		PutObjectRequest putRequest = this.getPutPublicObjectRequest(objectKey, is, metadata);

		// アップロード
		client.putObject(putRequest);

		return this.getObjectUrl(client, objectKey);
	}

	/**
	 * ファイルアップロード処理（マルチパートアップロード）
	 * ※プライベートオブジェクト
	 * 
	 * @param objectKey
	 * @param contentType
	 * @param is
	 * @param contentLength
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public void putPrivateObjectForMultiPartUpload(String objectKey, String contentType, InputStream is) throws Exception {

		// パートサイズを設定
		byte[] partSize = new byte[defaultPartSize];

		// クライアント生成
		AmazonS3 client = getClient();

		// ETagのリストを作成
		List<PartETag> partETags = new ArrayList<PartETag>();

		// メタデータ
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(contentType);

		// リクエスト情報
		PutObjectRequest putRequest = this.getPutPrivateObjectRequest(objectKey, is, metadata);

		// multipart upload開始.
		InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, objectKey);
		// アクセス許可設定
		initRequest.setCannedACL(putRequest.getCannedAcl());

		InitiateMultipartUploadResult initResponse = client.initiateMultipartUpload(initRequest);

		// InputStreamから１パートごとで送るサイズ分読み取りをする
		int bytesRead = 0;
		bytesRead = is.read(partSize);
		for (int i = 1; bytesRead > 0; i++) {

			// パートをアップロードするためのリクエストを作成
			UploadPartRequest uploadRequest = new UploadPartRequest()
					.withBucketName(bucketName)
					.withKey(objectKey)
					.withUploadId(initResponse.getUploadId())
					.withPartNumber(i)
					.withInputStream(new ByteArrayInputStream(partSize, 0, bytesRead))
					.withPartSize(bytesRead);

			// パートをアップロードし、返ってきたEタグをリストに格納する
			UploadPartResult uploadResult = client.uploadPart(uploadRequest);
			partETags.add(uploadResult.getPartETag());

			// 次に送信する分を読み込み
			bytesRead = is.read(partSize);
		}

		// multipart uploadの完了
		CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, objectKey, initResponse.getUploadId(), partETags);
		client.completeMultipartUpload(compRequest);
	}

	/**
	 * ファイルアップロード処理（マルチパートアップロード）
	 * ※パブリックオブジェクト
	 *
	 * @param objectKey
	 * @param contentType
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public String putPublicObjectForMultiPartUpload(String objectKey, String contentType, InputStream is) throws Exception {

		// パートサイズを設定
		byte[] partSize = new byte[defaultPartSize];

		// クライアント生成
		AmazonS3 client = getClient();

		// ETagのリストを作成
		List<PartETag> partETags = new ArrayList<PartETag>();

		// メタデータ
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(contentType);

		// リクエスト情報
		PutObjectRequest putRequest = this.getPutPublicObjectRequest(objectKey, is, metadata);

		// multipart upload開始.
		InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, objectKey);
		// アクセス許可設定
		initRequest.setCannedACL(putRequest.getCannedAcl());

		InitiateMultipartUploadResult initResponse = client.initiateMultipartUpload(initRequest);

		// file partsでアップロードする
		int bytesRead = 0;
		bytesRead = is.read(partSize);
		for (int i = 1; bytesRead > 0; i++) {

			// パートをアップロードするためのリクエストを作成
			UploadPartRequest uploadRequest = new UploadPartRequest()
					.withBucketName(bucketName)
					.withKey(objectKey)
					.withUploadId(initResponse.getUploadId())
					.withPartNumber(i)
					.withInputStream(new ByteArrayInputStream(partSize, 0, bytesRead))
					.withPartSize(bytesRead);

			// パートをアップロードし返ってきたEタグをリストに格納する
			UploadPartResult uploadResult = client.uploadPart(uploadRequest);
			partETags.add(uploadResult.getPartETag());

			// 次に送信する分を読み込み
			bytesRead = is.read(partSize);
		}

		// multipart uploadの完了
		CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, objectKey, initResponse.getUploadId(), partETags);
		client.completeMultipartUpload(compRequest);

		return this.getObjectUrl(client, objectKey);
	}

	// --------------------------------------------------
	// ダウンロード
	// --------------------------------------------------
	public S3ObjectInputStream getObjectContent(String objectKey) throws Exception {

		// クライアント生成
		AmazonS3 client = getClient();

		// ダウンロード
		S3Object s3Object = client.getObject(bucketName, objectKey);

		return s3Object.getObjectContent();
	}

	// --------------------------------------------------
	// ダウンロード
	// --------------------------------------------------
	public S3Object getObject(String objectKey) throws Exception {

		// クライアント生成
		AmazonS3 client = getClient();

		// ダウンロード
		S3Object s3Object = client.getObject(bucketName, objectKey);

		return s3Object;
	}

	// --------------------------------------------------
	// 一括削除
	// --------------------------------------------------
	public List<String> deleteObjects(List<String> objectKeys) throws Exception {

		// クライアント生成
		AmazonS3 client = getClient();

		List<KeyVersion> keys = new ArrayList<KeyVersion>();
		objectKeys.forEach(obj -> keys.add(new KeyVersion(obj)));

		// ファイル削除
		// ※S3上に存在しないオブジェクトのキーを渡して実行しても、エラーは発生せず正常終了する。
		DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName).withKeys(keys);
		DeleteObjectsResult result = client.deleteObjects(request);

		// 削除したオブジェクトのキーを取得
		List<String> deletedKeys = new ArrayList<String>();
		result.getDeletedObjects().forEach(obj -> deletedKeys.add(obj.getKey()));

		return deletedKeys;
	}

	// =========================================================================
	// private メソッド
	// =========================================================================
	// --------------------------------------------------
	// クライアント生成
	// --------------------------------------------------
	private AmazonS3 getClient() throws Exception {

		// 認証情報
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

		// クライアント設定
		ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setProtocol(Protocol.HTTPS); // プロトコル
		clientConfig.setConnectionTimeout(300000); // 接続タイムアウト(ms)
		clientConfig.setSocketTimeout(300000); // 転送タイムアウト(ms)

		// エンドポイント設定
		EndpointConfiguration endpointConfiguration = new EndpointConfiguration(endpointUrl, region);

		// クライアント生成
		AmazonS3 client = AmazonS3ClientBuilder.standard()
				.withPathStyleAccessEnabled(pathStyleAccessEnabled)
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withClientConfiguration(clientConfig)
				.withEndpointConfiguration(endpointConfiguration).build();

		return client;
	}

	/**
	 * アプリ以外からは非公開となるPutObjectRequestを取得<br>
	 * ※アプリからのみ参照・取得可能としたいオブジェクトの場合に使用する
	 *
	 * @param bucketName
	 * @param objectKey
	 * @param is
	 * @param metadata
	 * @return
	 */
	private PutObjectRequest getPutPrivateObjectRequest(String objectKey, InputStream is, ObjectMetadata metadata) {

		// リクエスト情報
		PutObjectRequest putRequest = this.getPutObjectRequest(objectKey, is, metadata);
		// 権限の設定
		putRequest.setCannedAcl(CannedAccessControlList.Private);

		return putRequest;
	}

	/**
	 * アプリ以外からも公開状態となるPutObjectRequestを取得<br>
	 * ※URLでの直接アクセスなど、アプリ以外から参照・取得を可能としたいオブジェクトの場合にのみ使用する
	 * ※オブジェクトの持つURLにリクエストすることで、誰でもオブジェクトの参照が可能となるため、使用する際は注意が必要
	 *
	 * @param bucketName
	 * @param objectKey
	 * @param is
	 * @param metadata
	 * @return
	 */
	private PutObjectRequest getPutPublicObjectRequest(String objectKey, InputStream is, ObjectMetadata metadata) {

		// リクエスト情報
		PutObjectRequest putRequest = this.getPutObjectRequest(objectKey, is, metadata);
		// 権限の設定
		putRequest.setCannedAcl(CannedAccessControlList.PublicRead);

		return putRequest;
	}

	/**
	 * PutObjectRequestを取得
	 *
	 * @param bucketName
	 * @param objectKey
	 * @param is
	 * @param metadata
	 * @return
	 */
	private PutObjectRequest getPutObjectRequest(String objectKey, InputStream is, ObjectMetadata metadata) {
		return new PutObjectRequest(bucketName, objectKey, is, metadata);
	}

	/**
	 * 対象オブジェクトのアクセスURLを取得
	 *
	 * @param client
	 * @param objectKey
	 * @return
	 */
	private String getObjectUrl(AmazonS3 client, String objectKey) {

		return client.getUrl(bucketName, objectKey).toString();
	}
}