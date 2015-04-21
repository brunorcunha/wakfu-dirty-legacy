package org.apache.tools.zip;

public class UnrecognizedExtraField implements CentralDirectoryParsingZipExtraField
{
    private ZipShort headerId;
    private byte[] localData;
    private byte[] centralData;
    
    public void setHeaderId(final ZipShort headerId) {
        this.headerId = headerId;
    }
    
    @Override
	public ZipShort getHeaderId() {
        return this.headerId;
    }
    
    public void setLocalFileDataData(final byte[] data) {
        this.localData = ZipUtil.copy(data);
    }
    
    @Override
	public ZipShort getLocalFileDataLength() {
        return new ZipShort(this.localData.length);
    }
    
    @Override
	public byte[] getLocalFileDataData() {
        return ZipUtil.copy(this.localData);
    }
    
    public void setCentralDirectoryData(final byte[] data) {
        this.centralData = ZipUtil.copy(data);
    }
    
    @Override
	public ZipShort getCentralDirectoryLength() {
        if (this.centralData != null) {
            return new ZipShort(this.centralData.length);
        }
        return this.getLocalFileDataLength();
    }
    
    @Override
	public byte[] getCentralDirectoryData() {
        if (this.centralData != null) {
            return ZipUtil.copy(this.centralData);
        }
        return this.getLocalFileDataData();
    }
    
    @Override
	public void parseFromLocalFileData(final byte[] data, final int offset, final int length) {
        final byte[] tmp = new byte[length];
        System.arraycopy(data, offset, tmp, 0, length);
        this.setLocalFileDataData(tmp);
    }
    
    @Override
	public void parseFromCentralDirectoryData(final byte[] data, final int offset, final int length) {
        final byte[] tmp = new byte[length];
        System.arraycopy(data, offset, tmp, 0, length);
        this.setCentralDirectoryData(tmp);
        if (this.localData == null) {
            this.setLocalFileDataData(tmp);
        }
    }
}
