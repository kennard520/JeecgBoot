package org.jeecg.modules.oss.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.CommonUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.util.oss.OssBootUtil;
import org.jeecg.common.util.oss.TencentCosUtil;
import org.jeecg.modules.oss.entity.OssFile;
import org.jeecg.modules.oss.mapper.OssFileMapper;
import org.jeecg.modules.oss.service.IOssFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Description: OSS云存储实现类
 * @author: jeecg-boot
 */
@Service("ossFileService")
public class OssFileServiceImpl extends ServiceImpl<OssFileMapper, OssFile> implements IOssFileService {

	@Value("${jeecg.uploadType}")
	private String uploadType;

	@Override
	public void upload(MultipartFile multipartFile) throws Exception {
		String fileName = multipartFile.getOriginalFilename();
		fileName = CommonUtils.getFileName(fileName);
		OssFile ossFile = new OssFile();
		ossFile.setFileName(fileName);
		String url = isTencentCosUploadType() ? TencentCosUtil.upload(multipartFile, "upload/test") : OssBootUtil.upload(multipartFile,"upload/test");
		if(oConvertUtils.isEmpty(url)){
			throw new JeecgBootException("上传文件失败! ");
		}
		// 返回阿里云原生域名前缀URL
		ossFile.setUrl(isTencentCosUploadType() ? url : OssBootUtil.getOriginalUrl(url));
		this.save(ossFile);
	}

	@Override
	public boolean delete(OssFile ossFile) {
		try {
			this.removeById(ossFile.getId());
			if (isTencentCosUploadType()) {
				TencentCosUtil.deleteUrl(ossFile.getUrl());
			} else {
				OssBootUtil.deleteUrl(ossFile.getUrl());
			}
		}
		catch (Exception ex) {
			log.error(ex.getMessage(),ex);
			return false;
		}
		return true;
	}

	private boolean isTencentCosUploadType() {
		return CommonConstant.UPLOAD_TYPE_TENCENT_COS.equals(uploadType) || CommonConstant.UPLOAD_TYPE_COS.equals(uploadType);
	}

}
