/*
 * ************* DO NOT EDIT THIS FILE ***********
 *
 * This file was automatically generated from IVpeDnD.idl.
 */


/**
 * Interface IVpeDnD
 *
 * IID: 0xA5EA4500-600F-4716-A17D-827EEF6BD126
 */

public interface IVpeDnD extends nsISupports
{
    public static final String IVPEDND_IID_STRING =
        "A5EA4500-600F-4716-A17D-827EEF6BD126";

    public static final nsID IVPEDND_IID =
        new nsID("A5EA4500-600F-4716-A17D-827EEF6BD126");


    /* void Init (in nsIDOMDocument aDOMDocument, in nsIPresShell aPresShell, in IVpeDragDropListener aListener, in AString pathSeparator); */
    public void init(nsIDOMDocument aDOMDocument, nsIPresShell aPresShell, IVpeDragDropListener aListener, OpaqueValue pathSeparator);

    /* void GetBounds (in nsIDOMNode aNode, out PRInt32 aX, out PRInt32 aY, out PRInt32 aWidth, out PRInt32 aHeight); */
    public void getBounds(nsIDOMNode aNode, int[] aX, int[] aY, int[] aWidth, int[] aHeight);

    /* void SetCursor (in AString aCursorName, in PRInt32 aLock); */
    public void setCursor(OpaqueValue aCursorName, int aLock);

}

/*
 * end
 */
